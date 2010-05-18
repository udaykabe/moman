package net.deuce.moman.om;

import net.deuce.moman.job.AbstractCommand;
import net.deuce.moman.job.Command;
import net.deuce.moman.util.Constants;
import net.deuce.moman.util.Utils;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import java.util.Arrays;
import java.util.Date;

@Service
public class PreferenceService extends UserBasedService<Preference, PreferenceDao> {

  @Autowired
  private PreferenceDao preferenceDao;

  @Autowired
  private UserService userService;

  protected PreferenceDao getDao() {
    return preferenceDao;
  }

  public void toXml(Preference preference, Element parent) {
    Element el = parent.addElement("preference");
    el.addAttribute("id", preference.getUuid());
    addElement(el, "type", preference.getType());
    addElement(el, "name", preference.getName());

    if (preference.getType().equals(Date.class.getName())) {
      Date d = new Date();
      d.setTime(Long.valueOf(preference.getValue()));
      addElement(el, "value", Constants.SHORT_DATE_FORMAT.format(d));
    } else {
      addElement(el, "value", preference.getValue());
    }
  }

  protected Preference getPreferenceByName(User user, String name) {
    return preferenceDao.getPreferenceByName(user, name);
  }

  public Class<Preference> getType() {
    return Preference.class;
  }

  public String getRootElementName() {
    return "preferences";
  }

  protected Preference getPreference(User user, String name) {
    String value = null;
    return getPreferenceByName(user, name);
  }

  protected void setPreference(User user, String type, String name, String value) {
    Preference p = getPreferenceByName(user, name);
    if (p == null) {
      if (value != null) {
        p = new Preference();
        p.setUuid(createUuid());
        p.setType(type);
        p.setName(name);
        p.setValue(value);
        p.setUser(user);
        saveOrUpdate(p);
      }
    } else if (value != null) {
      p.setValue(value);
      p.setType(type);
      saveOrUpdate(p);
    } else {
      delete(p);
    }
  }

  protected void validateType(Preference p, Class clazz) {
    if (!(p.getType().equals(clazz.getName()))) {
      throw new TypeMismatchException("Mismatched preference types. Asking for " + clazz.getName() + ", was : " + p.getType());
    }
  }

  protected Element buildResult(Preference p) {
    Element preferences = DocumentHelper.createElement("preferences");
    if (p != null) {
      toXml(p, preferences);
    }
    return preferences;
  }

  public Command getStringCommand(final String name) {

    final User user = userService.getDefaultUser();

    return new AbstractCommand(Preference.class.getSimpleName() + " getString()", true) {

      public void doExecute() throws Exception {
        Preference p = getPreference(user, name);
        if (p != null) {
          setResult(Arrays.asList(new Element[]{buildResult(p)}));
        } else {
          setResultCode(HttpServletResponse.SC_NOT_FOUND);
        }
      }
    };
  }

  public Command deletePreferenceCommand(final String name) {

    final User user = userService.getDefaultUser();

    return new AbstractCommand(Preference.class.getSimpleName() + " deletePreference()", true) {

      public void doExecute() throws Exception {
        final Preference p = getPreference(user, name);
        if (p != null) {
          setPreference(user, p.getType(), name, null);
          setUndo(new AbstractCommand("Undo " + getName(), true) {
            public void doExecute() throws Exception {
              undoSetPreference(user, name, p);
            }
          });
        } else {
          setResultCode(HttpServletResponse.SC_NOT_FOUND);
        }
      }
    };
  }

  public Command setStringCommand(final String name, final String value) {

    final User user = userService.getDefaultUser();

    return new AbstractCommand(Preference.class.getSimpleName() + " setString()", true) {

      public void doExecute() throws Exception {
        final Preference p = getPreference(user, name);
        setString(user, name, value);
        setUndo(new AbstractCommand("Undo " + getName(), true) {
          public void doExecute() throws Exception {
            undoSetPreference(user, name, p);
          }
        });
      }
    };
  }

  @Transactional
  public void undoSetPreference(User user, String name, Preference oldPref) {
    if (oldPref == null) {
      Preference newPref = getPreference(user, name);
      if (newPref != null) {
        delete(newPref);
      }
    } else {
      setPreference(user, oldPref.getType(), oldPref.getName(), oldPref.getValue());
    }
  }

  @Transactional
  public void setString(User user, String name, String value) {
    setPreference(user, String.class.getName(), name, value);
  }

  private Element buildError(String message) {
    Element error = DocumentHelper.createElement("error");
    error.addElement("message").setText(message);
    return error;
  }

  public Command getBooleanCommand(final String name) {

    final User user = userService.getDefaultUser();

    return new AbstractCommand(Preference.class.getSimpleName() + " getBoolean()", true) {

      public void doExecute() throws Exception {
        try {
          Preference p = getBoolean(user, name);
          if (p != null) {
            setResult(Arrays.asList(new Element[]{buildResult(p)}));
          } else {
            setResultCode(HttpServletResponse.SC_NOT_FOUND);
          }
        } catch (TypeMismatchException e) {
          setResultCode(HttpServletResponse.SC_BAD_REQUEST);
          setResult(Arrays.asList(new Element[]{buildError("Type mismatch: " + e.getMessage())}));
        }
      }
    };
  }

  public Command setBooleanCommand(final String name, final Boolean value) {

    final User user = userService.getDefaultUser();

    return new AbstractCommand(Preference.class.getSimpleName() + " setString()", true) {

      public void doExecute() throws Exception {
        final Preference p = getPreference(user, name);
        setBoolean(user, name, value);
        setUndo(new AbstractCommand("Undo " + getName(), true) {
          public void doExecute() throws Exception {
            undoSetPreference(user, name, p);
          }
        });
      }
    };
  }

  public Preference getBoolean(User user, String name) {
    Preference p = getPreference(user, name);
    if (p != null) {
      validateType(p, Boolean.class);
    }
    return p;
  }

  @Transactional
  public void setBoolean(User user, String name, Boolean value) {
    setPreference(user, Boolean.class.getName(), name, value.toString());
  }

  public Command getShortCommand(final String name) {

    final User user = userService.getDefaultUser();

    return new AbstractCommand(Preference.class.getSimpleName() + " getShort()", true) {

      public void doExecute() throws Exception {
        try {
          Preference p = getShort(user, name);
          if (p != null) {
            setResult(Arrays.asList(new Element[]{buildResult(p)}));
          } else {
            setResultCode(HttpServletResponse.SC_NOT_FOUND);
          }
        } catch (TypeMismatchException e) {
          setResultCode(HttpServletResponse.SC_BAD_REQUEST);
          setResult(Arrays.asList(new Element[]{buildError("Type mismatch: " + e.getMessage())}));
        }
      }
    };
  }

  public Command setShortCommand(final String name, final Short value) {

    final User user = userService.getDefaultUser();

    return new AbstractCommand(Preference.class.getSimpleName() + " setString()", true) {

      public void doExecute() throws Exception {
        final Preference p = getPreference(user, name);
        setShort(user, name, value);
        setUndo(new AbstractCommand("Undo " + getName(), true) {
          public void doExecute() throws Exception {
            undoSetPreference(user, name, p);
          }
        });
      }
    };
  }

  public Preference getShort(User user, String name) {
    Preference p = getPreference(user, name);
    if (p != null) {
      validateType(p, Short.class);
    }
    return p;
  }

  @Transactional
  public void setShort(User user, String name, Short value) {
    setPreference(user, Short.class.getName(), name, value.toString());
  }

  public Command getIntCommand(final String name) {

    final User user = userService.getDefaultUser();

    return new AbstractCommand(Preference.class.getSimpleName() + " getInt()", true) {

      public void doExecute() throws Exception {
        try {
          Preference p = getInt(user, name);
          if (p != null) {
            setResult(Arrays.asList(new Element[]{buildResult(p)}));
          } else {
            setResultCode(HttpServletResponse.SC_NOT_FOUND);
          }
        } catch (TypeMismatchException e) {
          setResultCode(HttpServletResponse.SC_BAD_REQUEST);
          setResult(Arrays.asList(new Element[]{buildError("Type mismatch: " + e.getMessage())}));
        }
      }
    };
  }

  public Command setIntCommand(final String name, final Integer value) {

    final User user = userService.getDefaultUser();

    return new AbstractCommand(Preference.class.getSimpleName() + " setString()", true) {

      public void doExecute() throws Exception {
        final Preference p = getPreference(user, name);
        setInt(user, name, value);
        setUndo(new AbstractCommand("Undo " + getName(), true) {
          public void doExecute() throws Exception {
            undoSetPreference(user, name, p);
          }
        });
      }
    };
  }

  public Preference getInt(User user, String name) {
    Preference p = getPreference(user, name);
    if (p != null) {
      validateType(p, Integer.class);
    }
    return p;
  }

  @Transactional
  public void setInt(User user, String name, Integer value) {
    setPreference(user, Integer.class.getName(), name, value.toString());
  }

  public Command getLongCommand(final String name) {

    final User user = userService.getDefaultUser();

    return new AbstractCommand(Preference.class.getSimpleName() + " getLong()", true) {

      public void doExecute() throws Exception {
        try {
          Preference p = getLong(user, name);
          if (p != null) {
            setResult(Arrays.asList(new Element[]{buildResult(p)}));
          } else {
            setResultCode(HttpServletResponse.SC_NOT_FOUND);
          }
        } catch (TypeMismatchException e) {
          setResultCode(HttpServletResponse.SC_BAD_REQUEST);
          setResult(Arrays.asList(new Element[]{buildError("Type mismatch: " + e.getMessage())}));
        }
      }
    };
  }

  public Command setLongCommand(final String name, final Long value) {

    final User user = userService.getDefaultUser();

    return new AbstractCommand(Preference.class.getSimpleName() + " setString()", true) {

      public void doExecute() throws Exception {
        final Preference p = getPreference(user, name);
        setLong(user, name, value);
        setUndo(new AbstractCommand("Undo " + getName(), true) {
          public void doExecute() throws Exception {
            undoSetPreference(user, name, p);
          }
        });
      }
    };
  }

  public Preference getLong(User user, String name) {
    Preference p = getPreference(user, name);
    if (p != null) {
      validateType(p, Long.class);
    }
    return p;
  }

  @Transactional
  public void setLong(User user, String name, Long value) {
    setPreference(user, Long.class.getName(), name, value.toString());
  }

  public Command getFloatCommand(final String name) {

    final User user = userService.getDefaultUser();

    return new AbstractCommand(Preference.class.getSimpleName() + " getFloat()", true) {

      public void doExecute() throws Exception {
        try {
          Preference p = getFloat(user, name);
          if (p != null) {
            setResult(Arrays.asList(new Element[]{buildResult(p)}));
          } else {
            setResultCode(HttpServletResponse.SC_NOT_FOUND);
          }
        } catch (TypeMismatchException e) {
          setResultCode(HttpServletResponse.SC_BAD_REQUEST);
          setResult(Arrays.asList(new Element[]{buildError("Type mismatch: " + e.getMessage())}));
        }
      }
    };
  }

  public Command setFloatCommand(final String name, final Float value) {

    final User user = userService.getDefaultUser();

    return new AbstractCommand(Preference.class.getSimpleName() + " setString()", true) {

      public void doExecute() throws Exception {
        final Preference p = getPreference(user, name);
        setFloat(user, name, value);
        setUndo(new AbstractCommand("Undo " + getName(), true) {
          public void doExecute() throws Exception {
            undoSetPreference(user, name, p);
          }
        });
      }
    };
  }

  public Preference getFloat(User user, String name) {
    Preference p = getPreference(user, name);
    if (p != null) {
      validateType(p, Float.class);
    }
    return p;
  }

  @Transactional
  public void setFloat(User user, String name, Float value) {
    setPreference(user, Float.class.getName(), name, value != null ? Utils.formatDouble(value.doubleValue()) : null);
  }

  public Command getDoubleCommand(final String name) {

    final User user = userService.getDefaultUser();

    return new AbstractCommand(Preference.class.getSimpleName() + " getDouble()", true) {

      public void doExecute() throws Exception {
        try {
          Preference p = getDouble(user, name);
          if (p != null) {
            setResult(Arrays.asList(new Element[]{buildResult(p)}));
          } else {
            setResultCode(HttpServletResponse.SC_NOT_FOUND);
          }
        } catch (TypeMismatchException e) {
          setResultCode(HttpServletResponse.SC_BAD_REQUEST);
          setResult(Arrays.asList(new Element[]{buildError("Type mismatch: " + e.getMessage())}));
        }
      }
    };
  }

  public Command setDoubleCommand(final String name, final Double value) {

    final User user = userService.getDefaultUser();

    return new AbstractCommand(Preference.class.getSimpleName() + " setString()", true) {

      public void doExecute() throws Exception {
        final Preference p = getPreference(user, name);
        setDouble(user, name, value);
        setUndo(new AbstractCommand("Undo " + getName(), true) {
          public void doExecute() throws Exception {
            undoSetPreference(user, name, p);
          }
        });
      }
    };
  }

  public Preference getDouble(User user, String name) {
    Preference p = getPreference(user, name);
    if (p != null) {
      validateType(p, Double.class);
    }
    return p;
  }

  @Transactional
  public void setDouble(User user, String name, Double value) {
    setPreference(user, Double.class.getName(), name, value != null ? Utils.formatDouble(value.doubleValue()) : null);
  }

  public Command getDateCommand(final String name) {

    final User user = userService.getDefaultUser();

    return new AbstractCommand(Preference.class.getSimpleName() + " getDate()", true) {

      public void doExecute() throws Exception {
        try {
          Preference p = getDate(user, name);
          if (p != null) {
            setResult(Arrays.asList(new Element[]{buildResult(p)}));
          } else {
            setResultCode(HttpServletResponse.SC_NOT_FOUND);
          }
        } catch (TypeMismatchException e) {
          setResultCode(HttpServletResponse.SC_BAD_REQUEST);
          setResult(Arrays.asList(new Element[]{buildError("Type mismatch: " + e.getMessage())}));
        }
      }
    };
  }

  public Command setDateCommand(final String name, final Date value) {

    final User user = userService.getDefaultUser();

    return new AbstractCommand(Preference.class.getSimpleName() + " setString()", true) {

      public void doExecute() throws Exception {
        final Preference p = getPreference(user, name);
        setDate(user, name, value);
        setUndo(new AbstractCommand("Undo " + getName(), true) {
          public void doExecute() throws Exception {
            undoSetPreference(user, name, p);
          }
        });
      }
    };
  }

  public Preference getDate(User user, String name) {
    Preference p = getPreference(user, name);
    if (p != null) {
      validateType(p, Date.class);
    }
    return p;
  }

  @Transactional
  public void setDate(User user, String name, Date value) {
    setPreference(user, Date.class.getName(), name, Long.toString(value.getTime()));
  }

}