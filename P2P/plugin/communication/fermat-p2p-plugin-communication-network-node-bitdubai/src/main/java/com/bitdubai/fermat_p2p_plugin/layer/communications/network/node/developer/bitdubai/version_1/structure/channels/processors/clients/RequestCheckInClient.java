/*
 * @#RequestCheckInClient.java - 2015
 * Copyright bitDubai.com., All rights reserved.
 * You may not modify, use, reproduce or distribute this software.
 * BITDUBAI/CONFIDENTIAL
 */
package com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.channels.processors.clients;

import com.bitdubai.fermat_api.layer.all_definition.network_service.enums.NetworkServiceType;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.data.Package;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.data.client.request.RequestProfileCheckInMsj;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.data.client.respond.RespondProfileCheckInMsj;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.profiles.ClientProfile;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.enums.MessageContentType;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.enums.PackageType;
import com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.channels.WebSocketChannelServerEndpoint;
import com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.channels.processors.PackageProcessor;
import com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.entities.CheckedClientsHistory;
import com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.entities.CheckedInClient;

import org.jboss.logging.Logger;

import java.io.IOException;

import javax.websocket.EncodeException;
import javax.websocket.Session;

/**
 * The Class <code>com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.channels.processors.clients.RequestCheckInClient</code>
 * process all messages received the type <code>PackageType.REQUEST_CHECK_IN_CLIENT</code><p/>
 * Created by Roberto Requena - (rart3001@gmail.com) on 06/12/15.
 *
 * @version 1.0
 * @since Java JDK 1.7
 */
public class RequestCheckInClient extends PackageProcessor {

    /**
     * Represent the LOG
     */
    private final Logger LOG = Logger.getLogger(RequestCheckInClient.class.getName());

    /**
     * Constructor whit parameter
     *
     * @param webSocketChannelServerEndpoint register
     */
    public RequestCheckInClient(WebSocketChannelServerEndpoint webSocketChannelServerEndpoint) {
        super(webSocketChannelServerEndpoint, PackageType.REQUEST_CHECK_IN_CLIENT);
    }

    /**
     * (non-javadoc)
     * @see PackageProcessor#processingPackage(Session, Package)
     */
    @Override
    public void processingPackage(Session session, Package packageReceived) {

        LOG.info("Processing new package received");

        String channelIdentityPrivateKey = getChannel().getChannelIdentity().getPrivateKey();
        String destinationIdentityPublicKey = (String) session.getUserProperties().get("");
        ClientProfile clientProfile = null;

        try {

            RequestProfileCheckInMsj messageContent = (RequestProfileCheckInMsj) packageReceived.getContent();

            /*
             * Validate if content type is the correct
             */
            if (messageContent.getMessageContentType() == MessageContentType.JSON){

                /*
                 * Obtain the profile of the client
                 */
                clientProfile = (ClientProfile) messageContent.getProfileToRegister();

                /*
                 * Create the CheckedInClient
                 */
                CheckedInClient checkedInClient = new CheckedInClient();
                checkedInClient.setIdentityPublicKey(clientProfile.getIdentityPublicKey());
                checkedInClient.setDeviceType(clientProfile.getDeviceType());

                //Validate if location are available
                if (clientProfile.getLocation() != null){
                    checkedInClient.setLatitude(clientProfile.getLocation().getLatitude());
                    checkedInClient.setLongitude(clientProfile.getLocation().getLongitude());
                }

                /*
                 * Save into the data base
                 */
                getDaoFactory().getCheckedInClientDao().create(checkedInClient);

                /*
                 * Create the CheckedClientsHistory
                 */
                CheckedClientsHistory checkedClientsHistory = new CheckedClientsHistory();
                checkedClientsHistory.setIdentityPublicKey(clientProfile.getIdentityPublicKey());
                checkedClientsHistory.setDeviceType(clientProfile.getDeviceType());
                checkedClientsHistory.setCheckType(CheckedClientsHistory.CHECK_TYPE_IN);

                //Validate if location are available
                if (clientProfile.getLocation() != null){
                    checkedClientsHistory.setLastLatitude(clientProfile.getLocation().getLatitude());
                    checkedClientsHistory.setLastLongitude(clientProfile.getLocation().getLongitude());
                }

                /*
                 * Save into the data base
                 */
                getDaoFactory().getCheckedClientsHistoryDao().create(checkedClientsHistory);

                /*
                 * If all ok, respond whit success message
                 */
                RespondProfileCheckInMsj respondProfileCheckInMsj = new RespondProfileCheckInMsj(clientProfile.getIdentityPublicKey(), RespondProfileCheckInMsj.STATUS.SUCCESS);
                Package packageRespond = Package.createInstance(respondProfileCheckInMsj, NetworkServiceType.UNDEFINED, PackageType.RESPOND_CHECK_IN_CLIENT, channelIdentityPrivateKey, destinationIdentityPublicKey);

                /*
                 * Send the respond
                 */
                session.getBasicRemote().sendObject(packageRespond);

            }

        }catch (Exception e){

            try {

                /*
                 * Respond whit fail message
                 */
                RespondProfileCheckInMsj respondProfileCheckInMsj = new RespondProfileCheckInMsj(clientProfile.getIdentityPublicKey(), RespondProfileCheckInMsj.STATUS.FAIL);
                Package packageRespond = Package.createInstance(respondProfileCheckInMsj, NetworkServiceType.UNDEFINED, PackageType.RESPOND_CHECK_IN_CLIENT, channelIdentityPrivateKey, destinationIdentityPublicKey);

                /*
                 * Send the respond
                 */
                session.getBasicRemote().sendObject(packageRespond);

            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (EncodeException e1) {
                e1.printStackTrace();
            }

        }

    }
}
