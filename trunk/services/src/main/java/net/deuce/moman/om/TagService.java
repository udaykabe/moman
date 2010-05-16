package net.deuce.moman.om;

import net.deuce.moman.util.Utils;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TagService extends UserBasedService<Tag, TagDao> {

  @Autowired
  private TagDao tagDao;

  protected TagDao getDao() {
    return tagDao;
  }

  public void toXml(Tag tag, Element parent) {
    Element el = parent.addElement("tag");
    el.addAttribute("id", tag.getUuid());
    addElement(el, "name", tag.getName());
    addElement(el, "enabled", tag.getEnabled());
  }

  public Class<Tag> getType() {
    return Tag.class;
  }

  public String getRootElementName() {
    return "tags";
  }

}