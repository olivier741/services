/*
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.tatsinktechnologic.smpp.gateway;

import com.tatsinktechnologic.configuration.Smpp_ConfigLoader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jsmpp.InvalidResponseException;
import org.jsmpp.PDUException;
import org.jsmpp.bean.Alphabet;
import org.jsmpp.bean.ESMClass;
import org.jsmpp.bean.MessageClass;
import org.jsmpp.bean.RegisteredDelivery;
import org.jsmpp.bean.SMSCDeliveryReceipt;
import org.jsmpp.extra.NegativeResponseException;
import org.jsmpp.extra.ResponseTimeoutException;
import org.jsmpp.extra.SessionState;
import org.jsmpp.session.SMPPSession;
import org.jsmpp.session.Session;
import org.jsmpp.session.SessionStateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Maciej Pigulski <maciej.pigulski[at]gmail.com>
 * 
 */
public class Submit_LongSMS {
	private static final Logger LOGGER = LoggerFactory.getLogger(Submit_LongSMS.class);

	private static final int MAX_MULTIPART_MSG_SEGMENT_SIZE_UCS2 = 134;
	private static final int MAX_SINGLE_MSG_SEGMENT_SIZE_UCS2 = 70;
	private static final int MAX_MULTIPART_MSG_SEGMENT_SIZE_7BIT = 154;
	private static final int MAX_SINGLE_MSG_SEGMENT_SIZE_7BIT = 160;
        
        
        private static Smpp_ConfigLoader configLoad = Smpp_ConfigLoader.getConfigurationLoader();

	private class SessionStateListenerImpl implements SessionStateListener {
		public void onStateChange(SessionState newState, SessionState oldState, Session source) {
			LOGGER.info("Session state changed from {} to {}", oldState , newState);
		}
	}

	private byte[][] splitUnicodeMessage(byte[] aMessage, Integer maximumMultipartMessageSegmentSize) {

		final byte UDHIE_HEADER_LENGTH = 0x05;
		final byte UDHIE_IDENTIFIER_SAR = 0x00;
		final byte UDHIE_SAR_LENGTH = 0x03;

		// determine how many messages have to be sent
		int numberOfSegments = aMessage.length / maximumMultipartMessageSegmentSize;
		int messageLength = aMessage.length;
		if (numberOfSegments > 255) {
			numberOfSegments = 255;
			messageLength = numberOfSegments * maximumMultipartMessageSegmentSize;
		}
		if ((messageLength % maximumMultipartMessageSegmentSize) > 0) {
			numberOfSegments++;
		}

		// prepare array for all of the msg segments
		byte[][] segments = new byte[numberOfSegments][];

		int lengthOfData;

		// generate new reference number
		byte[] referenceNumber = new byte[1];
		new Random().nextBytes(referenceNumber);

		// split the message adding required headers
		for (int i = 0; i < numberOfSegments; i++) {
			if (numberOfSegments - i == 1) {
				lengthOfData = messageLength - i * maximumMultipartMessageSegmentSize;
			} else {
				lengthOfData = maximumMultipartMessageSegmentSize;
			}

			// new array to store the header
			segments[i] = new byte[6 + lengthOfData];

			// UDH header
			// doesn't include itself, its header length
			segments[i][0] = UDHIE_HEADER_LENGTH;
			// SAR identifier
			segments[i][1] = UDHIE_IDENTIFIER_SAR;
			// SAR length
			segments[i][2] = UDHIE_SAR_LENGTH;
			// reference number (same for all messages)
			segments[i][3] = referenceNumber[0];
			// total number of segments
			segments[i][4] = (byte) numberOfSegments;
			// segment number
			segments[i][5] = (byte) (i + 1);

			// copy the data into the array
			System.arraycopy(aMessage, (i * maximumMultipartMessageSegmentSize), segments[i], 6, lengthOfData);

		}
		return segments;
	}

	public List<String> sendSMS(SMPPSession session,String user,String sourceMsisdn, String destinationMsisdn,String messageBody,ReceiveSMS rcvSMS) throws IOException, InterruptedException {
                List<String> listmessageID =null;
		MessageClass messageClass = configLoad.getMessageClass();
        
		session.addSessionStateListener(new SessionStateListenerImpl());
		session.setMessageReceiverListener(rcvSMS);

		// configure variables according to if message contains non-basic characters
		Alphabet alphabet = null;
		int maximumSingleMessageSize = 0;
		int maximumMultipartMessageSegmentSize = 0;
		byte[] byteSingleMessage = null;
		if (Gsm0338.isBasicEncodeable(messageBody)) {
			byteSingleMessage = messageBody.getBytes();
			alphabet = Alphabet.ALPHA_DEFAULT;
			maximumSingleMessageSize = MAX_SINGLE_MSG_SEGMENT_SIZE_7BIT;
			maximumMultipartMessageSegmentSize = MAX_MULTIPART_MSG_SEGMENT_SIZE_7BIT;
		} else {
			byteSingleMessage = messageBody.getBytes(configLoad.getCharset());
			alphabet = configLoad.getAlphabet();
			maximumSingleMessageSize = MAX_SINGLE_MSG_SEGMENT_SIZE_UCS2;
			maximumMultipartMessageSegmentSize = MAX_MULTIPART_MSG_SEGMENT_SIZE_UCS2;
		}

		// check if message needs splitting and set required sending parameters
		byte[][] byteMessagesArray = null;
		ESMClass esmClass = null;
		if (messageBody.length() > maximumSingleMessageSize) {
			// split message according to the maximum length of a segment
			byteMessagesArray = splitUnicodeMessage(byteSingleMessage, maximumMultipartMessageSegmentSize);
			// set UDHI so PDU will decode the header
			esmClass = new ESMClass(configLoad.getMessageMode(),configLoad.getMessageType(), configLoad.getGsmpecificFeature());
		} else {
			byteMessagesArray = new byte[][] { byteSingleMessage };
			esmClass = new ESMClass();
		}

		LOGGER.info("Sending message {}", messageBody);
		LOGGER.info("Message is {} characters long and will be sent as {} messages with params: {} {}",messageBody.length(), byteMessagesArray.length, alphabet, messageClass);

		// submit all messages
                listmessageID = new ArrayList<String>();
		for (int i = 0; i < byteMessagesArray.length; i++) {
			String messageId = submitMessage(   session, 
                                                            byteMessagesArray[i],
                                                            sourceMsisdn, 
                                                            destinationMsisdn,
                                                            alphabet,
                                                            esmClass);
                        listmessageID.add(messageId);
			LOGGER.info("Message submitted, message_id is {}", messageId);
		}

		LOGGER.info("Entering listening mode. Press enter to finish...");

          return listmessageID;
	}

	private String submitMessage(   SMPPSession session, 
                                        byte[] message, 
                                        String sourceMsisdn, 
                                        String destinationMsisdn,
                                        Alphabet alphabet, 
                                        ESMClass esmClass) {
		String messageId = null;
		try {
			messageId = session.submitShortMessage(configLoad.getService_type(), 
                                                               configLoad.getSourceAddrTon(), 
                                                               configLoad.getSourceAddrNpi(),
                                                               sourceMsisdn, 
                                                               configLoad.getDestAddrTon(), 
                                                               configLoad.getDestAddrNpi(), 
                                                               destinationMsisdn, 
                                                               esmClass,
                                                               (byte)configLoad.getProtocolId(), 
                                                               (byte)configLoad.getPriorityFlag(), 
                                                                configLoad.getScheduleDeliveryTime(), 
                                                                configLoad.getValidityPeriod(), 
                                                                new RegisteredDelivery(SMSCDeliveryReceipt.SUCCESS_FAILURE),
                                                                configLoad.getReplaceIfPresentFlag().value(), 
                                                                configLoad.getDataCoding(),
                                                                (byte)configLoad.getSmDefaultMsgId(), 
                                                                message);
		} catch (PDUException e) {
			// Invalid PDU parameter
			LOGGER.error("Invalid PDU parameter", e);
		} catch (ResponseTimeoutException e) {
			// Response timeout
			LOGGER.error("Response timeout", e);
		} catch (InvalidResponseException e) {
			// Invalid response
			LOGGER.error("Receive invalid response", e);
		} catch (NegativeResponseException e) {
			// Receiving negative response (non-zero command_status)
			LOGGER.error("Receive negative response", e);
		} catch (IOException e) {
			LOGGER.error("I/O error occured", e);
		}
		return messageId;
	}

}
