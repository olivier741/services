/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.resfull.handler;

/**
 *
 * @author olivier.tatsinkou
 */
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.apache.log4j.Logger;

public class HandlerRequest implements ContainerRequestFilter {

    private static Logger logger = Logger.getLogger(HandlerRequest.class);

    @Override
    public void filter(ContainerRequestContext req) throws IOException {
        logger.info("filter!");
      
      

    }

    private void abortWithUnauthorized(ContainerRequestContext requestContext, Exception e) {

        // Abort the filter chain with a 401 status code response
        // The WWW-Authenticate header is sent along with the response
        requestContext.abortWith(Response.status(Response.Status.BAD_REQUEST)
                .header(HttpHeaders.WWW_AUTHENTICATE,  e.getMessage() )
                .build());
    }

    private boolean checkIP(String ip_address) {

        Pattern ptn = Pattern.compile("\\|");
        List<String> listIP = Arrays.asList(ptn.split(ip_address));

        Message message = PhaseInterceptorChain.getCurrentMessage();
        HttpServletRequest request = (HttpServletRequest) message.get(AbstractHTTPDestination.HTTP_REQUEST);
        String callerIpAddress = request.getRemoteAddr();
        IpAddressMatcher ipAddressMacher = new IpAddressMatcher(listIP);
        logger.info("Client IP Address = " + callerIpAddress);

        return ipAddressMacher.matches(callerIpAddress);
    }

    private final class IpAddressMatcher {

        private final List<Integer> listnMaskBits;
        private final List<InetAddress> listrequiredAddress;

        /**
         * Takes a specific IP address or a range specified using the IP/Netmask
         * (e.g. 192.168.1.0/24 or 202.24.0.0/14).
         *
         * @param ipAddress the address or range of addresses from which the
         * request must come.
         */
        public IpAddressMatcher(List<String> listIpAddress) {
            listnMaskBits = new ArrayList<Integer>();
            listrequiredAddress = new ArrayList<InetAddress>();

            for (String ipAddress : listIpAddress) {
                int nMaskBits;
                if (ipAddress.indexOf('/') > 0) {
                    String[] addressAndMask = ipAddress.split("/");
                    ipAddress = addressAndMask[0];
                    nMaskBits = Integer.parseInt(addressAndMask[1]);
                } else {
                    nMaskBits = -1;
                }
                listnMaskBits.add(nMaskBits);
                listrequiredAddress.add(parseAddress(ipAddress));
            }

        }

        public boolean matches(String address) {
            boolean result = false;
            int numberIP = listrequiredAddress.size();
            InetAddress remoteAddress = parseAddress(address);

            for (int i = 0; i < numberIP; i++) {

                if (!listrequiredAddress.get(i).getClass().equals(remoteAddress.getClass())) {
                    continue;
                }

                if (listnMaskBits.get(i) < 0) {
                    if (remoteAddress.equals(listrequiredAddress.get(i))) {
                        result = true;
                        break;
                    } else {
                        continue;
                    }
                }

                byte[] remAddr = remoteAddress.getAddress();
                byte[] reqAddr = listrequiredAddress.get(i).getAddress();

                int oddBits = listnMaskBits.get(i) % 8;
                int nMaskBytes = listnMaskBits.get(i) / 8 + (oddBits == 0 ? 0 : 1);
                byte[] mask = new byte[nMaskBytes];

                Arrays.fill(mask, 0, oddBits == 0 ? mask.length : mask.length - 1, (byte) 0xFF);

                if (oddBits != 0) {
                    int finalByte = (1 << oddBits) - 1;
                    finalByte <<= 8 - oddBits;
                    mask[mask.length - 1] = (byte) finalByte;
                }

                boolean isNOK = true;
                for (int j = 0; j < mask.length; j++) {
                    if ((remAddr[j] & mask[j]) != (reqAddr[j] & mask[j])) {
                        isNOK = false;
                        break;
                    }
                }

                if (!isNOK) {
                    continue;
                } else {
                    result = true;
                    break;
                }

            }
            return result;
        }

        private InetAddress parseAddress(String address) {
            InetAddress result = null;
            try {
                result = InetAddress.getByName(address);
            } catch (UnknownHostException e) {
                logger.error("Failed to parse address" + address, e);
            }
            return result;
        }
    }

}
