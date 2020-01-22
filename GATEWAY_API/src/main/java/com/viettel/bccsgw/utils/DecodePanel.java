//package com.viettel.bccsgw.utils;
// 
// import java.awt.event.ActionEvent;
// import java.awt.event.ActionListener;
// import java.io.BufferedReader;
// import java.io.File;
// import java.io.FileInputStream;
// import java.io.FileNotFoundException;
// import java.io.FileOutputStream;
// import java.io.FileReader;
// import java.io.FileWriter;
// import java.io.IOException;
// import javax.swing.GroupLayout;
// import javax.swing.GroupLayout.Alignment;
// import javax.swing.GroupLayout.ParallelGroup;
// import javax.swing.GroupLayout.SequentialGroup;
// import javax.swing.JButton;
// import javax.swing.JFileChooser;
// import javax.swing.JLabel;
// import javax.swing.JPanel;
// import javax.swing.JScrollPane;
// import javax.swing.JTextArea;
// import javax.swing.JTextField;
// import javax.swing.LayoutStyle.ComponentPlacement;
// 
// public class DecodePanel
//   extends JPanel
// {
//   private JFileChooser fDlg;
//   private ByteBuffer buff;
//   private JButton jButton1;
//   private JButton jButton2;
//   private JButton jButton3;
//   private JButton jButton4;
//   private JButton jButton5;
//   private JButton jButton6;
//   private JLabel jLabel1;
//   private JLabel jLabel2;
//   private JScrollPane jScrollPane1;
//   private JScrollPane jScrollPane2;
//   private JTextArea jTextArea1;
//   private JTextArea jTextArea2;
//   private JTextField jTextField1;
//   private JTextField jTextField2;
//   
//   public DecodePanel()
//   {
//     initComponents();
//     this.fDlg = new JFileChooser();
//     this.buff = new ByteBuffer();
//   }
//   
//   private void initComponents()
//   {
//     this.jScrollPane2 = new JScrollPane();
//     this.jTextArea2 = new JTextArea();
//     this.jScrollPane1 = new JScrollPane();
//     this.jTextArea1 = new JTextArea();
//     this.jLabel2 = new JLabel();
//     this.jLabel1 = new JLabel();
//     this.jTextField1 = new JTextField();
//     this.jTextField2 = new JTextField();
//     this.jButton2 = new JButton();
//     this.jButton4 = new JButton();
//     this.jButton1 = new JButton();
//     this.jButton3 = new JButton();
//     this.jButton5 = new JButton();
//     this.jButton6 = new JButton();
//     
//     this.jTextArea2.setColumns(20);
//     this.jTextArea2.setLineWrap(true);
//     this.jTextArea2.setRows(5);
//     this.jScrollPane2.setViewportView(this.jTextArea2);
//     
//     this.jTextArea1.setColumns(20);
//     this.jTextArea1.setLineWrap(true);
//     this.jTextArea1.setRows(5);
//     this.jScrollPane1.setViewportView(this.jTextArea1);
//     
//     this.jLabel2.setText("Hex File");
//     
//     this.jLabel1.setText("Binary File");
//     
//     this.jButton2.setText("open");
//     this.jButton2.addActionListener(new ActionListener()
//     {
//       public void actionPerformed(ActionEvent evt)
//       {
//         DecodePanel.this.jButton2ActionPerformed(evt);
//       }
//     });
//     this.jButton4.setText("save");
//     this.jButton4.addActionListener(new ActionListener()
//     {
//       public void actionPerformed(ActionEvent evt)
//       {
//         DecodePanel.this.jButton4ActionPerformed(evt);
//       }
//     });
//     this.jButton1.setText("open");
//     this.jButton1.addActionListener(new ActionListener()
//     {
//       public void actionPerformed(ActionEvent evt)
//       {
//         DecodePanel.this.jButton1ActionPerformed(evt);
//       }
//     });
//     this.jButton3.setText("save");
//     this.jButton3.addActionListener(new ActionListener()
//     {
//       public void actionPerformed(ActionEvent evt)
//       {
//         DecodePanel.this.jButton3ActionPerformed(evt);
//       }
//     });
//     this.jButton5.setText("To Binary");
//     this.jButton5.addActionListener(new ActionListener()
//     {
//       public void actionPerformed(ActionEvent evt)
//       {
//         DecodePanel.this.jButton5ActionPerformed(evt);
//       }
//     });
//     this.jButton6.setText("To Hex");
//     this.jButton6.addActionListener(new ActionListener()
//     {
//       public void actionPerformed(ActionEvent evt)
//       {
//         DecodePanel.this.jButton6ActionPerformed(evt);
//       }
//     });
//     GroupLayout layout = new GroupLayout(this);
//     setLayout(layout);
//     layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jScrollPane1, -1, 526, 32767).addComponent(this.jScrollPane2, -1, 526, 32767).addGroup(layout.createSequentialGroup().addComponent(this.jLabel1).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jTextField1, -1, 316, 32767).addGap(6, 6, 6).addComponent(this.jButton1).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addComponent(this.jButton3).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 25, 32767))).addGap(0, 0, 0)).addGroup(layout.createSequentialGroup().addGap(21, 21, 21).addComponent(this.jLabel2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jTextField2, -1, 316, 32767).addGap(6, 6, 6).addComponent(this.jButton2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jButton4).addGap(29, 29, 29)).addGroup(layout.createSequentialGroup().addGap(145, 145, 145).addComponent(this.jButton5).addGap(18, 18, 18).addComponent(this.jButton6).addContainerGap(225, 32767)));
//     
// 
// 
// 
// 
// 
// 
// 
// 
// 
// 
// 
// 
// 
// 
// 
// 
// 
// 
// 
// 
// 
// 
// 
// 
// 
// 
// 
// 
// 
// 
// 
// 
//     layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel1).addComponent(this.jTextField1, -2, -1, -2).addComponent(this.jButton1).addComponent(this.jButton3)).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addComponent(this.jScrollPane1, -2, 129, -2).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jButton5).addComponent(this.jButton6)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 9, 32767).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel2).addComponent(this.jTextField2, -2, -1, -2).addComponent(this.jButton2).addComponent(this.jButton4)).addGap(18, 18, 18).addComponent(this.jScrollPane2, -2, 151, -2).addContainerGap()));
//   }
//   
//   private void jButton1ActionPerformed(ActionEvent evt)
//   {
//     int op = this.fDlg.showOpenDialog(this);
//     if (op == 0)
//     {
//       this.jTextField1.setText(this.fDlg.getSelectedFile().getPath());
//       this.jTextArea1.setText("");
//       byte[] b = readBinaryFile(this.jTextField1.getText());
//       this.buff.setBuffer(b);
//       for (int i = 0; i < b.length; i++) {
//         this.jTextArea1.append(String.valueOf(b[i]));
//       }
//       String hex = Hex.encode(b);
//       this.jTextArea2.setText(hex.toUpperCase());
//     }
//   }
//   
//   private void jButton2ActionPerformed(ActionEvent evt)
//   {
//     int op = this.fDlg.showOpenDialog(this);
//     if (op == 0)
//     {
//       this.jTextField2.setText(this.fDlg.getSelectedFile().getPath());
//       String text = readTextFile(this.jTextField2.getText());
//       this.jTextArea2.setText(text.toUpperCase());
//       byte[] b = Hex.decode(text);
//       this.buff.setBuffer(b);
//       this.jTextArea1.setText("");
//       for (int i = 0; i < b.length; i++) {
//         this.jTextArea1.append(String.valueOf(b[i]));
//       }
//     }
//   }
//   
//   private void jButton3ActionPerformed(ActionEvent evt)
//   {
//     int op = this.fDlg.showSaveDialog(this);
//     if (op == 0)
//     {
//       this.jTextField1.setText(this.fDlg.getSelectedFile().getPath());
//       String text = this.jTextArea1.getText();
//       try
//       {
//         FileOutputStream w = new FileOutputStream(this.jTextField1.getText());
//         for (int i = 0; i < text.length(); i++) {
//           w.write(this.buff.getBuffer());
//         }
//         w.close();
//       }
//       catch (IOException ex)
//       {
//         ex.printStackTrace();
//       }
//     }
//   }
//   
//   private void jButton4ActionPerformed(ActionEvent evt)
//   {
//     int op = this.fDlg.showSaveDialog(this);
//     if (op == 0)
//     {
//       this.jTextField2.setText(this.fDlg.getSelectedFile().getPath());
//       String text = this.jTextArea2.getText();
//       try
//       {
//         FileWriter w = new FileWriter(this.jTextField2.getText());
//         w.write(text);
//         w.close();
//       }
//       catch (IOException ex)
//       {
//         ex.printStackTrace();
//       }
//     }
//   }
//   
//   private void jButton6ActionPerformed(ActionEvent evt)
//   {
//     byte[] b = this.buff.getBuffer();
//     String hex = Hex.encode(b);
//     this.jTextArea2.setText(hex);
//   }
//   
//   private void jButton5ActionPerformed(ActionEvent evt)
//   {
//     String text = this.jTextArea2.getText();
//     byte[] b = Hex.decode(text);
//     this.buff.setBuffer(b);
//     this.jTextArea1.setText("");
//     for (int i = 0; i < b.length; i++) {
//       this.jTextArea1.append(String.valueOf(b[i]));
//     }
//   }
//   
//   private byte[] readBinaryFile(String cFile)
//   {
//     try
//     {
//       ByteBuffer buff = new ByteBuffer();
//       byte[] b = new byte[1024];
//       FileInputStream in = new FileInputStream(cFile);
//       int byteReaded;
//       while ((byteReaded = in.read(b)) > 0) {
//         buff.appendBytes(b, byteReaded);
//       }
//       in.close();
//       return buff.getBuffer();
//     }
//     catch (FileNotFoundException ex)
//     {
//       ex.printStackTrace();
//     }
//     catch (IOException ex)
//     {
//       ex.printStackTrace();
//     }
//     return new byte[0];
//   }
//   
//   private String readTextFile(String cFile)
//   {
//     try
//     {
//       StringBuffer buff = new StringBuffer();
//       FileReader in = new FileReader(cFile);
//       BufferedReader r = new BufferedReader(in);
//       String str;
//       while ((str = r.readLine()) != null) {
//         buff.append(str);
//       }
//       r.close();
//       return buff.toString();
//     }
//     catch (FileNotFoundException ex)
//     {
//       ex.printStackTrace();
//     }
//     catch (IOException ex)
//     {
//       ex.printStackTrace();
//     }
//     return "";
//   }
// }
//
//
//
