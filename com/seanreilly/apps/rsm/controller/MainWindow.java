package com.seanreilly.apps.rsm.controller;

import java.awt.*;
import javax.swing.*;

public class MainWindow
  extends JFrame
{
  private Main main = null;
  
  MainWindow(Main m) {
    this.main = m;

    getContentPane().add("Center", new InputPanel(main));

    pack();

    Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
    setLocation(500, 250);
    setSize(400, 400);
  }


}

  
