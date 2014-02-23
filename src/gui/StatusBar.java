/*
 * Copyright (C) 2001-2003 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package oxdoc.gui;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import java.awt.*;

/**
 * This is a statusbar component with a text control for messages.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class StatusBar extends JPanel {
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  /**
   * Message to display if there is no msg to display. Defaults to a blank
   * string.
   */
  private String msgWhenEmpty = " ";

  /**
   * Label showing the message in the statusbar.
   */
  private final JLabel textLbl = new JLabel();

  private final JProgressBar progressBar = new JProgressBar();

  private final JPanel pnlLabelOrProgress = new JPanel();

  /**
   * Constraints used to add new controls to this statusbar.
   */
  private final GridBagConstraints gridBagConstraints = new GridBagConstraints();

  private Font _font;

  /**
   * Default ctor.
   */
  public StatusBar() {
    super(new GridBagLayout());
    createGUI();
  }

  /**
   * Set the font for controls in this statusbar.
   *
   * @param font The font to use.
   * @throws IllegalArgumentException Thrown if <TT>null</TT> <TT>Font</TT> passed.
   */
  public synchronized void setFont(Font font) {
    if (font == null)
      throw new IllegalArgumentException("Font == null");
    super.setFont(font);
    _font = font;
    updateSubcomponentsFont(this);
  }

  /**
   * Set the text to display in the message label.
   *
   * @param text Text to display in the message label.
   */
  public synchronized void setText(String text) {
    String myText = null;
    if (text != null)
      myText = text.trim();
    if (myText != null && myText.length() > 0)
      textLbl.setText(myText);
    else
      clearText();
  }

  /**
   * Returns the text label's current value
   */
  public synchronized String getText() {
    return textLbl.getText();
  }

  public synchronized void clearText() {
    textLbl.setText(msgWhenEmpty);
  }

  public synchronized void setTextWhenEmpty(String value) {
    final boolean wasEmpty = textLbl.getText().equals(msgWhenEmpty);
    if (value != null && value.length() > 0)
      msgWhenEmpty = value;
    else
      msgWhenEmpty = " ";
    if (wasEmpty)
      clearText();
  }

  public synchronized void addJComponent(JComponent comp) {
    if (comp == null)
      throw new IllegalArgumentException("JComponent == null");
    comp.setBorder(createComponentBorder());
    if (_font != null) {
      comp.setFont(_font);
      updateSubcomponentsFont(comp);
    }
    super.add(comp, gridBagConstraints);
  }

  public static Border createComponentBorder() {
    return BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED),
        BorderFactory.createEmptyBorder(0, 4, 0, 4));
  }

  private void createGUI() {
    clearText();

    Dimension progSize = progressBar.getPreferredSize();
    progSize.height = textLbl.getPreferredSize().height;
    progressBar.setPreferredSize(progSize);

    progressBar.setStringPainted(true);

    pnlLabelOrProgress.setLayout(new GridLayout(1, 1));
    pnlLabelOrProgress.add(textLbl);

    // The message area is on the right of the statusbar and takes
    // up all available space.
    gridBagConstraints.anchor = GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridx = 0;
    addJComponent(pnlLabelOrProgress);

    // Any other components are on the right.
    gridBagConstraints.weightx = 0.0;
    gridBagConstraints.anchor = GridBagConstraints.CENTER;
    gridBagConstraints.gridx = GridBagConstraints.RELATIVE;
  }

  private void updateSubcomponentsFont(Container cont) {
    Component[] comps = cont.getComponents();
    for (Component comp : comps) {
      comp.setFont(_font);
      if (comp instanceof Container)
        updateSubcomponentsFont((Container) comp);
    }
  }

  public void setStatusBarProgress(String msg, int minimum, int maximum, int value) {
    if (!(pnlLabelOrProgress.getComponent(0) instanceof JProgressBar)) {
      pnlLabelOrProgress.remove(0);
      pnlLabelOrProgress.add(progressBar);
      validate();
    }

    progressBar.setMinimum(minimum);
    progressBar.setMaximum(maximum);
    progressBar.setValue(value);

    if (null != msg)
      progressBar.setString(msg);
    else
      progressBar.setString("");
  }

  public void setStatusBarProgressFinished() {
    if (pnlLabelOrProgress.getComponent(0) instanceof JProgressBar) {
      pnlLabelOrProgress.remove(0);
      pnlLabelOrProgress.add(textLbl);
      validate();
      repaint();
    }
  }
}
