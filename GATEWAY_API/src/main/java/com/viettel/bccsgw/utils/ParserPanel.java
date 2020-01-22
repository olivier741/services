//package com.viettel.bccsgw.utils;
// 
// import java.awt.Color;
// import java.awt.event.ActionEvent;
// import java.awt.event.ActionListener;
// import javax.swing.BorderFactory;
// import javax.swing.GroupLayout;
// import javax.swing.GroupLayout.Alignment;
// import javax.swing.GroupLayout.ParallelGroup;
// import javax.swing.GroupLayout.SequentialGroup;
// import javax.swing.JButton;
// import javax.swing.JLabel;
// import javax.swing.JPanel;
// import javax.swing.JScrollPane;
// import javax.swing.JTable;
// import javax.swing.JTextArea;
// import javax.swing.JTextField;
// import javax.swing.LayoutStyle.ComponentPlacement;
// import javax.swing.table.DefaultTableModel;
// 
// public class ParserPanel
//   extends JPanel
// {
//   private JButton jButton1;
//   private JLabel jLabel2;
//   private JPanel jPanel1;
//   private JScrollPane jScrollPane1;
//   private JScrollPane jScrollPane3;
//   private JTable jTable1;
//   private JTextArea jTextArea1;
//   private JTextField jTextField3;
//   
//   public ParserPanel()
//   {
//     initComponents();
//   }
//   
//   private void initComponents()
//   {
//     this.jLabel2 = new JLabel();
//     this.jButton1 = new JButton();
//     this.jTextField3 = new JTextField();
//     this.jPanel1 = new JPanel();
//     this.jScrollPane3 = new JScrollPane();
//     this.jTable1 = new JTable();
//     this.jScrollPane1 = new JScrollPane();
//     this.jTextArea1 = new JTextArea();
//     
//     this.jLabel2.setText("Expression");
//     
//     this.jButton1.setText("Execute");
//     this.jButton1.addActionListener(new ActionListener()
//     {
//       public void actionPerformed(ActionEvent evt)
//       {
//         ParserPanel.this.jButton1ActionPerformed(evt);
//       }
//     });
//     this.jTextField3.setHorizontalAlignment(0);
//     this.jTextField3.setToolTipText("result of parser");
//     
//     this.jPanel1.setBorder(BorderFactory.createTitledBorder("Param"));
//     
//     this.jTable1.setModel(new DefaultTableModel(new Object[][] { { null, null }, { null, null }, { null, null }, { null, null }, { null, null } }, new String[] { "Param", "Value" })
//     {
//       Class[] types = { String.class, String.class };
//       
//       public Class getColumnClass(int columnIndex)
//       {
//         return this.types[columnIndex];
//       }
//     });
//     this.jScrollPane3.setViewportView(this.jTable1);
//     
//     GroupLayout jPanel1Layout = new GroupLayout(this.jPanel1);
//     this.jPanel1.setLayout(jPanel1Layout);
//     jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addComponent(this.jScrollPane3, -2, -1, -2).addContainerGap(-1, 32767)));
//     
// 
//     jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jScrollPane3, -2, 104, -2));
//     
//     this.jTextArea1.setColumns(20);
//     this.jTextArea1.setRows(5);
//     this.jScrollPane1.setViewportView(this.jTextArea1);
//     
//     GroupLayout layout = new GroupLayout(this);
//     setLayout(layout);
//     layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(this.jPanel1, -1, -1, 32767).addContainerGap()).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(this.jLabel2).addContainerGap(434, 32767)).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(this.jScrollPane1, -1, 476, 32767).addContainerGap()).addGroup(layout.createSequentialGroup().addGap(163, 163, 163).addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(this.jTextField3, GroupLayout.Alignment.LEADING, -1, 153, 32767).addComponent(this.jButton1, GroupLayout.Alignment.LEADING, -1, 153, 32767)).addGap(180, 180, 180)));
//     
//     layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(this.jPanel1, -2, -1, -2).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addComponent(this.jLabel2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jScrollPane1, -2, 106, -2).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addComponent(this.jButton1).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jTextField3, -2, -1, -2).addContainerGap(-1, 32767)));
//   }
//   
//   private void jButton1ActionPerformed(ActionEvent evt)
//   {
//     Rule r = new Rule();
//     for (int i = 0; i < this.jTable1.getRowCount(); i++)
//     {
//       String param = (String)this.jTable1.getValueAt(i, 0);
//       if ((param != null) && (param.length() > 0))
//       {
//         String value = (String)this.jTable1.getValueAt(i, 1);
//         if (value == null) {
//           value = "";
//         }
//         r.setVariable(param.trim(), value.trim());
//       }
//     }
//     String expression = this.jTextArea1.getText().trim();
//     r.setExpression(expression);
//     
//     boolean b = r.parser();
//     if (b)
//     {
//       this.jTextField3.setBackground(Color.GREEN);
//       this.jTextField3.setForeground(Color.WHITE);
//       this.jTextField3.setText("True");
//     }
//     else
//     {
//       this.jTextField3.setBackground(Color.RED);
//       this.jTextField3.setForeground(Color.WHITE);
//       this.jTextField3.setText("False");
//     }
//   }
// }
//
//
