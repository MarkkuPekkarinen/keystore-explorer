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

package org.kse.gui.dialogs.sign;

import java.awt.Component;
import java.math.BigInteger;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;

import org.kse.utilities.StringUtils;

/**
 * Custom cell renderer for the cells of list certificates of a keystore table of JListCertificates.
 */
public class ListCertsTableCellRend extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 1L;

    @Override
    public Component getTableCellRendererComponent(JTable jtRevokedCerts, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int col) {
        JLabel cell = (JLabel) super.getTableCellRendererComponent(jtRevokedCerts, value, isSelected, hasFocus, row,
                                                                   col);

        if (col == 0) {
            cell.setText((String) value);
        } else if (col == 1) {
            cell.setText(formatSerialNumberAsHexString((BigInteger) value));
        } else {
            cell.setText(StringUtils.formatDate((Date) value));
        }

        cell.setBorder(new EmptyBorder(0, 5, 0, 5));

        return cell;
    }

    private String formatSerialNumberAsHexString(BigInteger serialNumber) {
        // The string is divided by spaces into groups of four hex characters.
        String hexSerialNumber = serialNumber.toString(16).toUpperCase();

        StringBuilder strBuff = new StringBuilder();

        strBuff.append("0x");

        for (int i = 0; i < hexSerialNumber.length(); i++) {
            strBuff.append(hexSerialNumber.charAt(i));

            if ((i + 1) % 4 == 0 && i + 1 != hexSerialNumber.length()) {
                strBuff.append(' ');
            }
        }

        return strBuff.toString();
    }

}
