package uenp.kettle.Fato;

import org.pentaho.di.i18n.BaseMessages;


public class Messages {

	public static final Class<Messages> PKG = Messages.class;
	
	public static String getString(String key){
		return BaseMessages.getString(PKG, key);
	}
	
	public static String getString(String key, String aux1){
		return BaseMessages.getString(PKG, key, aux1);
	}
	
	public static String getString(String key, String aux1, String aux2){
		return BaseMessages.getString(PKG, key, aux1, aux2);
	}
	
	public static String getString(String key, String aux1, String aux2, String aux3){
		return BaseMessages.getString(PKG, key, aux1, aux2, aux3);
	}
	
	public static String getString(String key, String aux1, String aux2, String aux3, String aux4){
		return BaseMessages.getString(PKG, key, aux1, aux2, aux3, aux4);
	}
	
	public static String getString(String key, String aux1, String aux2, String aux3, String aux4, String aux5){
		return BaseMessages.getString(PKG, key, aux1, aux2, aux3, aux4, aux5);
	}
	
	public static String getString(String key, String aux1, String aux2, String aux3, String aux4, String aux5, String aux6){
		return BaseMessages.getString(PKG, key, aux1, aux2, aux3, aux4, aux5, aux6);
	}
}

