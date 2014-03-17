package com.simplyapped.libgdx.ext.ui;

import javax.swing.JOptionPane;

public class DesktopOSDialog implements OSDialog {

	@Override
	public void showShortToast(CharSequence message) {
		ToastMessage toastMessage = new ToastMessage(message.toString(),1000);
        toastMessage.setVisible(true);
	}

	@Override
	public void showLongToast(CharSequence message) {
		ToastMessage toastMessage = new ToastMessage(message.toString(),3000);
        toastMessage.setVisible(true);
	}

	@Override
	public void showAlertBox(String alertBoxTitle, String alertBoxMessage,
			String alertBoxButtonText) {
		JOptionPane.showMessageDialog(null, alertBoxMessage, alertBoxTitle, JOptionPane.INFORMATION_MESSAGE);
	}

}
