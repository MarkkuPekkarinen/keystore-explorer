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
package org.kse.gui.crypto.generalsubtree;

import java.awt.Component;
import java.util.ResourceBundle;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 * Custom cell renderer for the headers of the general subtrees table.
 */
public class GeneralSubtreesTableHeadRend extends DefaultTableCellRenderer {
    private static final long serialVersionUID = 1L;

    private static ResourceBundle res = ResourceBundle.getBundle("org/kse/gui/crypto/generalsubtree/resources");

    private TableCellRenderer delegate;

    public GeneralSubtreesTableHeadRend(TableCellRenderer delegate) {
        this.delegate = delegate;
    }

    /**
     * Returns the rendered header cell for the supplied value and column.
     *
     * @param jTable     The JTable
     * @param value      The value to assign to the cell
     * @param isSelected True if cell is selected
     * @param row        The row of the cell to render
     * @param col        The column of the cell to render
     * @param hasFocus   If true, render cell appropriately
     * @return The rendered cell
     */
    @Override
    public Component getTableCellRendererComponent(JTable jTable, Object value, boolean isSelected, boolean hasFocus,
                                                   int row, int col) {

        Component c = delegate.getTableCellRendererComponent(jTable, value, isSelected, hasFocus, row, col);

        if (c instanceof JLabel) {

            JLabel header = (JLabel) c;

            if (col == 0) {
                header.setToolTipText(res.getString("GeneralSubtreesTableHeadRend.BaseColumn.tooltip"));
            } else if (col == 1) {
                header.setToolTipText(res.getString("GeneralSubtreesTableHeadRend.MinimumColumn.tooltip"));
            } else {
                header.setToolTipText(res.getString("GeneralSubtreesTableHeadRend.MaximumColumn.tooltip"));
            }

        }

        return c;
    }
}
