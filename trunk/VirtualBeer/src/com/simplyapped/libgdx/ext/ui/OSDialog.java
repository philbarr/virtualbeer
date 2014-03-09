package com.simplyapped.libgdx.ext.ui;

public interface OSDialog {
	    public void showShortToast(CharSequence toastMessage);
	    public void showLongToast(CharSequence toastMessage);
	    public void showAlertBox(String alertBoxTitle, String alertBoxMessage, String alertBoxButtonText);
}
