/*
 * Copyright 2004 - 2013 Wayne Grant
 *           2013 - 2025 Kai Kramer
 *
 * This file is part of KeyStore Explorer.
 *
 * KeyStore Explorer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KeyStore Explorer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with KeyStore Explorer.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kse.gui.dialogs;

import static org.kse.utilities.StringUtils.addToList;

import java.awt.Container;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

import org.kse.gui.passwordmanager.Password;
import org.kse.gui.components.JEscDialog;
import org.kse.gui.KseFrame;
import org.kse.gui.MiGUtil;
import org.kse.gui.PlatformUtil;
import org.kse.gui.actions.OpenAction;
import org.kse.gui.preferences.PreferencesManager;
import org.kse.gui.preferences.data.KsePreferences;
import org.kse.utilities.DialogViewer;
import org.kse.utilities.history.KeyStoreHistory;

import net.miginfocom.swing.MigLayout;

/**
 * Dialog used for SSL connection details (host and port) to examine.
 */
public class DExamineSsl extends JEscDialog {
    private static final long serialVersionUID = 1L;
    private static ResourceBundle res = ResourceBundle.getBundle("org/kse/gui/dialogs/resources");

    private transient KsePreferences preferences = PreferencesManager.getPreferences();
    public static final int MAX_LIST_SIZE = 10;

    private static final String CANCEL_KEY = "CANCEL_KEY";

    private JLabel jlSslHost;
    private JComboBox<String> jcbSslHost;
    private JLabel jlSslPort;
    private JComboBox<String> jcbSslPort;
    private JCheckBox jcbClientAuth;
    private JComboBox<KeyStoreHistory> jcbKeyStore;
    private JButton jbLoadKeystore;
    private JPanel jpButtons;
    private JButton jbOK;
    private JButton jbCancel;

    private String sslHost;
    private int sslPort = -1;

    private KseFrame kseFrame;
    private boolean cancelled = true;

    /**
     * Creates new DExamineSsl dialog.
     *
     * @param parent Parent frame
     */
    public DExamineSsl(JFrame parent, KseFrame kseFrame) {
        super(parent, res.getString("DExamineSsl.Title"), Dialog.ModalityType.DOCUMENT_MODAL);
        this.kseFrame = kseFrame;
        initComponents();
    }

    private void initComponents() {
        jlSslHost = new JLabel(res.getString("DExamineSsl.jlSslHost.text"));

        jcbSslHost = new JComboBox<>();
        jcbSslHost.setEditable(true);
        jcbSslHost.setPrototypeDisplayValue("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        jcbSslHost.setToolTipText(res.getString("DExamineSsl.jtfSslHost.tooltip"));
        jcbSslHost.setModel(new DefaultComboBoxModel<>(getSslHosts()));

        jlSslPort = new JLabel(res.getString("DExamineSsl.jlSslPort.text"));

        jcbSslPort = new JComboBox<>();
        jcbSslPort.setEditable(true);
        jcbSslPort.setToolTipText(res.getString("DExamineSsl.jtfSslPort.tooltip"));
        jcbSslPort.setModel(new DefaultComboBoxModel<>(getSslPorts()));

        jcbClientAuth = new JCheckBox(res.getString("DExamineSsl.jlEnableClientAuth.text"));

        jcbKeyStore = new JComboBox<>(getKeystoreNames());
        jcbKeyStore.setToolTipText(res.getString("DExamineSsl.jcbKeyStore.tooltip"));

        jbLoadKeystore = new JButton();
        jbLoadKeystore.setIcon(new ImageIcon(getClass().getResource("images/open.png")));
        jbLoadKeystore.setToolTipText(res.getString("DExamineSsl.jbLoadKeystore.tooltip"));

        jbOK = new JButton(res.getString("DExamineSsl.jbOK.text"));

        jbCancel = new JButton(res.getString("DExamineSsl.jbCancel.text"));
        jbCancel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), CANCEL_KEY);

        jpButtons = PlatformUtil.createDialogButtonPanel(jbOK, jbCancel, "insets 0");

        Container pane = getContentPane();
        pane.setLayout(new MigLayout("insets dialog, fill", "[indent]unrel[right]unrel[grow]", ""));
        MiGUtil.addSeparator(pane, res.getString("DExamineSsl.jlConnSettings.text"));
        pane.add(jlSslHost, "skip");
        pane.add(jcbSslHost, "sgx, growx, wrap");
        pane.add(jlSslPort, "skip");
        pane.add(jcbSslPort, "sgx, growx, wrap para");
        MiGUtil.addSeparator(pane, res.getString("DExamineSsl.jlClientAuth.text"));
        pane.add(jcbClientAuth, "left, spanx, wrap");
        pane.add(new JLabel(res.getString("DExamineSsl.jlKeyStore.text")), "skip");
        pane.add(jcbKeyStore, "sgx, growx");
        pane.add(jbLoadKeystore, "growx, wrap para");
        pane.add(new JSeparator(), "spanx, growx, wrap");
        pane.add(jpButtons, "right, spanx");

        jcbClientAuth.addActionListener(evt -> updateClientAuthComponents());
        jbLoadKeystore.addActionListener(evt -> {
            OpenAction openAction = new OpenAction(kseFrame);
            openAction.actionPerformed(evt);
            updateClientAuthComponents();
        });
        jbOK.addActionListener(evt -> okPressed());
        jbCancel.addActionListener(evt -> cancelPressed());

        jbCancel.getActionMap().put(CANCEL_KEY, new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent evt) {
                cancelPressed();
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                closeDialog();
            }
        });

        updateClientAuthComponents();

        getRootPane().setDefaultButton(jbOK);

        setResizable(false);

        pack();
    }

    private ComboBoxModel<KeyStoreHistory> getKeystoreNames() {
        KeyStoreHistory[] keyStoreHistories = kseFrame.getKeyStoreHistories();
        return new DefaultComboBoxModel<>(keyStoreHistories);
    }

    /**
     * Get selected SSL hostname.
     *
     * @return Hostname or null of dialog cancelled by user
     */
    public String getSslHost() {
        return sslHost;
    }

    /**
     * Get selected SSL port number.
     *
     * @return Port number or -1 if dialog cancelled by user
     */
    public int getSslPort() {
        return sslPort;
    }

    /**
     * User wants to use SSL client authentication?
     *
     * @return True if user wants to use SSL client authentication
     */
    public boolean useClientAuth() {
        return jcbClientAuth.isSelected();
    }

    /**
     * Get selected key store
     *
     * @return KeyStore (wrapped in a history object)
     */
    public KeyStoreHistory getKeyStore() {
        return (KeyStoreHistory) jcbKeyStore.getSelectedItem();
    }

    /**
     * Was the dialog cancelled?
     *
     * @return True if it was cancelled
     */
    public boolean wasCancelled() {
        return cancelled;
    }

    private String[] getSslHosts() {
        List<String> sslHosts = preferences.getExamineSslHosts();
        return sslHosts.toArray(new String[sslHosts.size()]);
    }

    private String[] getSslPorts() {
        List<String> sslPorts = preferences.getExamineSslPorts();
        return sslPorts.toArray(new String[sslPorts.size()]);
    }

    private void updateClientAuthComponents() {
        jcbKeyStore.setEnabled(jcbClientAuth.isSelected());
        jbLoadKeystore.setEnabled(jcbClientAuth.isSelected());

        jcbKeyStore.setModel(getKeystoreNames());
    }

    private void okPressed() {
        String sslHost = ((String) jcbSslHost.getSelectedItem()).trim();

        if (sslHost.isEmpty()) {
            JOptionPane.showMessageDialog(this, res.getString("DExamineSsl.SslHostReq.message"), getTitle(),
                                          JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sslPortStr = ((String) jcbSslPort.getSelectedItem()).trim();

        if (sslPortStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, res.getString("DExamineSsl.SslPortReq.message"), getTitle(),
                                          JOptionPane.WARNING_MESSAGE);
            return;
        }

        int sslPort = 0;

        try {
            sslPort = Integer.parseInt(sslPortStr);

            if (sslPort < 1) {
                JOptionPane.showMessageDialog(this, res.getString("DExamineSsl.PositiveIntegerSslPortReq.message"),
                                              getTitle(), JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, res.getString("DExamineSsl.PositiveIntegerSslPortReq.message"),
                                          getTitle(), JOptionPane.WARNING_MESSAGE);
            return;
        }

        this.sslHost = sslHost;
        this.sslPort = sslPort;

        // check selected key store
        if (useClientAuth()) {
            KeyStoreHistory ksh = (KeyStoreHistory) jcbKeyStore.getSelectedItem();
            if (ksh == null) {
                JOptionPane.showMessageDialog(this, res.getString("DExamineSsl.NoKeyStoreSelected.message"), getTitle(),
                                              JOptionPane.WARNING_MESSAGE);
                return;
            }

            Password keyStorePassword = ksh.getCurrentState().getPassword();
            if (keyStorePassword == null && ksh.getCurrentState().getType().hasEntryPasswords()) {
                JOptionPane.showMessageDialog(this, res.getString("DExamineSsl.NoPasswordSetForKeyStore.message"),
                                              getTitle(), JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        // save host/port in preferences
        preferences.setExamineSslHosts(addToList(sslHost, preferences.getExamineSslHosts(), MAX_LIST_SIZE));
        preferences.setExamineSslPorts(addToList(sslPortStr, preferences.getExamineSslPorts(), MAX_LIST_SIZE));

        cancelled = false;
        closeDialog();
    }

    private void cancelPressed() {
        closeDialog();
    }

    private void closeDialog() {
        setVisible(false);
        dispose();
    }

    // for quick testing
    public static void main(String[] args) throws Exception {
        DialogViewer.run(new DExamineSsl(new javax.swing.JFrame(), new KseFrame()));
    }
}
