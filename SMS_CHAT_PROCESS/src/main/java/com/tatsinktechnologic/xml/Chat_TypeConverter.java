///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.tatsinktechnologic.xml;
//
//import org.simpleframework.xml.convert.Converter;
//import org.simpleframework.xml.stream.InputNode;
//import org.simpleframework.xml.stream.OutputNode;
//
///**
// *
// * @author olivier.tatsinkou
// */
//public class Chat_TypeConverter implements Converter<Chat_Type> {
//   
//    @Override
//    public Chat_Type read(InputNode node) throws Exception
//    {
//        final String value = node.getNext("chat_process").getAttribute("type").getValue();
//
//        // Decide what enum it is by its value
//        for( Chat_Type ts : Chat_Type.values() )
//        {
//            if( ts.getStatus().equalsIgnoreCase(value) )
//                return ts;
//        }
//        throw new IllegalArgumentException("No enum available for " + value);
//    }
//
//
//    @Override
//    public void write(OutputNode node, Chat_Type value) throws Exception
//    {
//        // You can customize your xml here (example structure like your xml)
//        OutputNode child = node.getChild("status");
//        child.setValue(value.getStatus());
//    }
//
//}
