///*
// * Copyright 2019 olivier.tatsinkou.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.viettel.bccsgw.sendmt;
//
///**
// *
// * @author olivier.tatsinkou
// */
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import javax.swing.DefaultComboBoxModel;
//import javax.swing.GroupLayout;
//import javax.swing.GroupLayout.Alignment;
//import javax.swing.GroupLayout.ParallelGroup;
//import javax.swing.GroupLayout.SequentialGroup;
//import javax.swing.JButton;
//import javax.swing.JComboBox;
//import javax.swing.JLabel;
//import javax.swing.JPanel;
//import javax.swing.JScrollPane;
//import javax.swing.JTextArea;
//import javax.swing.JTextField;
//import javax.swing.LayoutStyle.ComponentPlacement;
//
//public class SendPanel
//  extends JPanel
//{
//  MtStub stub;
//  private JButton btReset;
//  private JButton btSend;
//  private JComboBox cbUrl;
//  private JLabel jLabel1;
//  private JLabel jLabel2;
//  private JLabel jLabel3;
//  private JLabel jLabel4;
//  private JLabel jLabel5;
//  private JLabel jLabel6;
//  private JScrollPane jScrollPane1;
//  private JLabel lbStatus;
//  private JTextField tfFrom;
//  private JTextField tfPassword;
//  private JTextField tfTo;
//  private JTextField tfUsername;
//  private JTextField tfXmlns;
//  private JTextArea txContent;
//  
//  public SendPanel()
//  {
//    initComponents();
//  }
//  
//  private void initComponents()
//  {
//    this.jLabel1 = new JLabel();
//    this.cbUrl = new JComboBox();
//    this.jLabel2 = new JLabel();
//    this.tfXmlns = new JTextField();
//    this.jLabel3 = new JLabel();
//    this.jLabel4 = new JLabel();
//    this.tfUsername = new JTextField();
//    this.tfPassword = new JTextField();
//    this.jLabel5 = new JLabel();
//    this.tfFrom = new JTextField();
//    this.jLabel6 = new JLabel();
//    this.tfTo = new JTextField();
//    this.btSend = new JButton();
//    this.jScrollPane1 = new JScrollPane();
//    this.txContent = new JTextArea();
//    this.btReset = new JButton();
//    this.lbStatus = new JLabel();
//    
//    this.jLabel1.setText("Url");
//    
//    this.cbUrl.setEditable(true);
//    this.cbUrl.setModel(new DefaultComboBoxModel(new String[] { "http://192.168.176.190:8008/vasp/Service.asmx", "http://192.168.176.152:8008/vasp/Service.asmx", "http://127.0.0.1:8008/vasp/Service.asmx" }));
//    
//    this.jLabel2.setText("Xmlns");
//    
//    this.tfXmlns.setText("http://tempuri.org/");
//    
//    this.jLabel3.setText("Username");
//    
//    this.jLabel4.setText("Password");
//    
//    this.tfUsername.setText("sms2008");
//    
//    this.tfPassword.setText("sms2008");
//    
//    this.jLabel5.setText("From");
//    
//    this.jLabel6.setText("To");
//    
//    this.tfTo.setText("84");
//    
//    this.btSend.setText("Send");
//    this.btSend.addActionListener(new ActionListener()
//    {
//      public void actionPerformed(ActionEvent evt)
//      {
//        SendPanel.this.btSendActionPerformed(evt);
//      }
//    });
//    this.txContent.setColumns(20);
//    this.txContent.setRows(5);
//    this.jScrollPane1.setViewportView(this.txContent);
//    
//    this.btReset.setText("Reset");
//    this.btReset.addActionListener(new ActionListener()
//    {
//      public void actionPerformed(ActionEvent evt)
//      {
//        SendPanel.this.btResetActionPerformed(evt);
//      }
//    });
//    GroupLayout layout = new GroupLayout(this);
//    setLayout(layout);
//    layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(this.jLabel5).addComponent(this.jLabel1).addComponent(this.jLabel2).addComponent(this.jLabel3).addComponent(this.jLabel4)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false).addComponent(this.cbUrl, -2, 319, -2).addGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false).addComponent(this.tfPassword, GroupLayout.Alignment.LEADING).addComponent(this.tfUsername, GroupLayout.Alignment.LEADING).addComponent(this.tfXmlns, GroupLayout.Alignment.LEADING, -1, 111, 32767).addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup().addComponent(this.tfFrom, -2, 62, -2).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addComponent(this.jLabel6).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addComponent(this.tfTo, -2, 113, -2))).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.btReset).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.btSend)).addComponent(this.jScrollPane1)).addContainerGap(-1, 32767)).addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addContainerGap(190, 32767).addComponent(this.lbStatus).addGap(223, 223, 223)));
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
//
//    layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel1).addComponent(this.cbUrl, -2, -1, -2)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel2).addComponent(this.tfXmlns, -2, -1, -2)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(this.jLabel3).addComponent(this.tfUsername, -2, -1, -2)).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel4).addComponent(this.tfPassword, -2, -1, -2)).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel5).addComponent(this.btSend).addComponent(this.tfFrom, -2, -1, -2).addComponent(this.jLabel6).addComponent(this.tfTo, -2, -1, -2).addComponent(this.btReset)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.lbStatus).addGap(6, 6, 6).addComponent(this.jScrollPane1, -1, 127, 32767).addContainerGap()));
//  }
//  
//  private void btResetActionPerformed(ActionEvent evt)
//  {
//    this.stub = null;
//  }
//  
//  private void btSendActionPerformed(ActionEvent evt)
//  {
//    if (this.stub == null)
//    {
//      String url = (String)this.cbUrl.getSelectedItem();
//      String xmlns = this.tfXmlns.getText();
//      String user = this.tfUsername.getText();
//      String pass = this.tfPassword.getText();
//      this.stub = new MtStub(url, xmlns, user, pass);
//    }
//    String sender = this.tfFrom.getText();
//    String receiver = this.tfTo.getText();
//    String content = this.txContent.getText();
//    int error = this.stub.send("0", "sendmt", sender, receiver, "0", content, "1");
//    this.lbStatus.setText("Send Result=" + error);
//  }
//}
//
