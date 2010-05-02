package net.deuce.moman.controller;

public interface Actions {
  public static int NEW_ENTITY = 0;
  public static int EDIT_ENTITY = 1;
  public static int GET_ENTITY = 2;
  public static int LIST_ENTITIES = 3;
  public static int DELETE_ENTITY = 4;
  public static int LIST_ENTITY_PROPERTIES = 5;
  public static int LIST_SERVICE_COMMANDS = 6;
  public static int EXECUTE_COMMAND = 7;

  public static int JOB_STATUS = 8;
  public static int UNDO_COMMAND = 9;
  public static int REDO_COMMAND = 10;

  public static int NEW_DISCONNECTED_ACCOUNT = 11;
  public static int IMPORT_ACCOUNT_TRANSACTIONS = 12;

  public static int LIST_CUSTOM_TRANSACTIONS = 13;

  public static int GET_ENTITY_PROPERTY = 14;
}
