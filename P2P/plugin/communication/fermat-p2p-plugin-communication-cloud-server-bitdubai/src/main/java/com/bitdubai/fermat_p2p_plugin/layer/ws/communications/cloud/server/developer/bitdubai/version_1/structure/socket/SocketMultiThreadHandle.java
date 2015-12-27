/*
* @#SocketMultiThreadHandle.java - 2015
* Copyright bitDubai.com., All rights reserved.
 * You may not modify, use, reproduce or distribute this software.
* BITDUBAI/CONFIDENTIAL
*/
package com.bitdubai.fermat_p2p_plugin.layer.ws.communications.cloud.server.developer.bitdubai.version_1.structure.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.bitdubai.fermat_api.layer.all_definition.components.enums.PlatformComponentType;
import com.bitdubai.fermat_api.layer.all_definition.components.interfaces.DiscoveryQueryParameters;
import com.bitdubai.fermat_api.layer.all_definition.components.interfaces.PlatformComponentProfile;
import com.bitdubai.fermat_api.layer.all_definition.network_service.enums.NetworkServiceType;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.components.DiscoveryQueryParametersCommunication;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.components.PlatformComponentProfileCommunication;
import com.bitdubai.fermat_p2p_api.layer.p2p_communication.commons.enums.JsonAttNamesConstants;
import com.bitdubai.fermat_p2p_plugin.layer.ws.communications.cloud.server.developer.bitdubai.version_1.structure.WsCommunicationCloudServer;
import com.bitdubai.fermat_p2p_plugin.layer.ws.communications.cloud.server.developer.bitdubai.version_1.structure.util.DistanceCalculator;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

/**
 * The Class <code>com.bitdubai.fermat_p2p_plugin.layer.ws.communications.cloud.server.developer.bitdubai.version_1.structure.socket.SocketMultiThreadHandle</code>
 * <p/>
 * Created by Hendry Rodriguez - (elnegroevaristo@gmail.com) on 20/11/15.
 *
 * @version 1.0
 * @since Java JDK 1.7
 */
public class SocketMultiThreadHandle extends Thread{

    private Socket socket;

    /**
     * Represent the wsCommunicationCloudServer
     */
    private WsCommunicationCloudServer wsCommunicationCloudServer;

    public SocketMultiThreadHandle(Socket clientSocket, WsCommunicationCloudServer wsCommunicationCloudServer) {
        this.socket = clientSocket;
        this.wsCommunicationCloudServer = wsCommunicationCloudServer;
    }


    public void run() {
        InputStream inp = null;
        BufferedReader brinp = null;
        PrintWriter out = null;

        try {
            inp = socket.getInputStream();
            brinp = new BufferedReader(new InputStreamReader(inp));
            out = new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            return;
        }

        while (true) {
            try {
                String line = brinp.readLine();

                if ((line == null || line =="")) {

                    socket.close();
                    return;

                } else  if(line.contains(JsonAttNamesConstants.NAME_IDENTITY) && line.contains(JsonAttNamesConstants.DISCOVERY_PARAM) ){

                    JsonObject jsonObjectRespond = new JsonObject();

                    Gson gson = new Gson();
                    JsonParser parser = new JsonParser();
                    JsonObject jsonObject =  parser.parse(line).getAsJsonObject();

                    try{

                        String clientIdentityPublicKey = jsonObject.get(JsonAttNamesConstants.NAME_IDENTITY).getAsString();

                        if(wsCommunicationCloudServer.getRegisteredClientConnectionsCache().containsKey(clientIdentityPublicKey)) {

                            DiscoveryQueryParameters discoveryQueryParameters = new DiscoveryQueryParametersCommunication().
                                    fromJson(jsonObject.get(JsonAttNamesConstants.DISCOVERY_PARAM).getAsString());

                            /*
                             * hold the result list
                             */
                            List<PlatformComponentProfile> resultList = null;

                            if (discoveryQueryParameters.getFromOtherPlatformComponentType() == null &&
                                    discoveryQueryParameters.getFromOtherNetworkServiceType() == null) {

                                resultList = applyDiscoveryQueryParameters(discoveryQueryParameters, clientIdentityPublicKey);

                            } else {

                                resultList = applyDiscoveryQueryParametersFromOtherComponent(discoveryQueryParameters, clientIdentityPublicKey);

                            }

                            System.out.println("ComponentRegisteredListSocket - filteredLis.size() = " + resultList.size());

                            /*
                             * Convert the list to json representation
                             */
                            String jsonListRepresentation = gson.toJson(resultList, new TypeToken<List<PlatformComponentProfileCommunication>>() {
                            }.getType());

                            /*
                             * Create the respond
                            */
                            jsonObjectRespond.addProperty(JsonAttNamesConstants.RESULT_LIST, jsonListRepresentation);


                        }else{

                            System.out.println("ComponentRegisteredListSocket - Requested list is not available \n" +
                                    " Cause : Client is not registered! *(");
                            jsonObjectRespond.addProperty(JsonAttNamesConstants.FAILURE, "Requested list is not available \n Cause : Client is not registered! *(");

                        }

                    }catch (Exception e){

                        System.out.println("ComponentRegisteredListSocket - requested list is not available");
                        jsonObjectRespond.addProperty(JsonAttNamesConstants.FAILURE, "Requested list is not available \n Cause: "+e.getCause()+"\n Message: "+e.getMessage());
                       // e.printStackTrace();
                    }

                    String jsonString = gson.toJson(jsonObjectRespond);

                    out.println(jsonString.trim());
                    out.flush();

                    socket.close();
                    return;

                }else{
                    out.println("{\"message\":\"Security Violation *(\"}");
                    out.flush();

                    socket.close();
                    return;
                }

            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }



    /**
     * Return the primary list from the cache filtered by the platformComponentType or
     * networkServiceType
     *
     * @param platformComponentType
     * @param networkServiceType
     * @return List<PlatformComponentProfile>
     */
    public List<PlatformComponentProfile> getPrimaryFilteredListFromCache(PlatformComponentType platformComponentType, NetworkServiceType networkServiceType, String clientIdentityPublicKey){

        /*
         * Get the list
         */
        List<PlatformComponentProfile> list = new ArrayList<>();

         /*
         * Switch between platform component type
         */
        switch (platformComponentType){

            case COMMUNICATION_CLOUD_SERVER :
                if (!wsCommunicationCloudServer.getRegisteredCommunicationsCloudServerCache().isEmpty()){
                    list = (List<PlatformComponentProfile>) new ArrayList<>(wsCommunicationCloudServer.getRegisteredCommunicationsCloudServerCache().values()).clone();
                }
                break;

            case COMMUNICATION_CLOUD_CLIENT :
                if (!wsCommunicationCloudServer.getRegisteredCommunicationsCloudClientCache().isEmpty()){
                    list = (List<PlatformComponentProfile>) new ArrayList<>(wsCommunicationCloudServer.getRegisteredCommunicationsCloudClientCache().values()).clone();
                }
                break;

            case NETWORK_SERVICE :
                if (wsCommunicationCloudServer.getRegisteredNetworkServicesCache().containsKey(networkServiceType) && !wsCommunicationCloudServer.getRegisteredNetworkServicesCache().get(networkServiceType).isEmpty()){
                    list = (List<PlatformComponentProfile>) new ArrayList<>(wsCommunicationCloudServer.getRegisteredNetworkServicesCache().get(networkServiceType)).clone();
                }
                break;

            //Others
            default :
                if (wsCommunicationCloudServer.getRegisteredOtherPlatformComponentProfileCache().containsKey(platformComponentType) && !wsCommunicationCloudServer.getRegisteredOtherPlatformComponentProfileCache().get(platformComponentType).isEmpty()) {
                    list = (List<PlatformComponentProfile>) new ArrayList<>(wsCommunicationCloudServer.getRegisteredOtherPlatformComponentProfileCache().get(platformComponentType)).clone();
                }
                break;
        }

        /*
         * Remove the requester from the list
         */
        Iterator<PlatformComponentProfile> iterator = list.iterator();
        while (iterator.hasNext()){

            PlatformComponentProfile platformComponentProfileRegistered = iterator.next();
            if(platformComponentProfileRegistered.getCommunicationCloudClientIdentity().equals(clientIdentityPublicKey)){
                System.out.println("ComponentRegisteredListWebService - removing ="+platformComponentProfileRegistered.getName());
                iterator.remove();
            }
        }


        return list;
    }


    /**
     * Filter the PlatformComponentProfile that match with the discoveryQueryParameters
     *
     * @param discoveryQueryParameters
     * @return List<PlatformComponentProfile>
     */
    private  List<PlatformComponentProfile> applyDiscoveryQueryParameters(DiscoveryQueryParameters discoveryQueryParameters, String clientIdentityPublicKey){

        int totalFilterToApply = countFilers(discoveryQueryParameters);
        int filterMatched = 0;

        List<PlatformComponentProfile>  list = getPrimaryFilteredListFromCache(discoveryQueryParameters.getPlatformComponentType(), discoveryQueryParameters.getNetworkServiceType(), clientIdentityPublicKey);
        List<PlatformComponentProfile>  filteredLis = new ArrayList<>();

        System.out.println("ComponentRegisteredListWebService - totalFilterToApply    = "+totalFilterToApply);

        if (totalFilterToApply > 0){

            /*
             * Apply the basic filter
             */
            for (PlatformComponentProfile platformComponentProfile: list) {

                if (discoveryQueryParameters.getIdentityPublicKey() != null && discoveryQueryParameters.getIdentityPublicKey() != ""){
                    if (platformComponentProfile.getIdentityPublicKey().equals(discoveryQueryParameters.getIdentityPublicKey())){
                        filterMatched += 1;
                    }
                }

                if (discoveryQueryParameters.getAlias() != null && discoveryQueryParameters.getAlias() != ""){
                    if (discoveryQueryParameters.getAlias().toLowerCase().contains(platformComponentProfile.getAlias().toLowerCase())){
                        filterMatched += 1;
                    }
                }

                if (discoveryQueryParameters.getName() != null && discoveryQueryParameters.getName() != ""){
                    if (discoveryQueryParameters.getName().toLowerCase().contains(platformComponentProfile.getName().toLowerCase())){
                        filterMatched += 1;
                    }
                }

                if (discoveryQueryParameters.getExtraData() != null && discoveryQueryParameters.getExtraData() != ""){
                    if (discoveryQueryParameters.getExtraData().toLowerCase().contains(platformComponentProfile.getExtraData().toLowerCase())){
                        filterMatched += 1;
                    }
                }

                //if all filter matched
                if (totalFilterToApply == filterMatched){
                    //Add to the list
                    filteredLis.add(platformComponentProfile);
                }

            }

        }else {

            filteredLis = list;
        }

        /*
         * Apply geo location filter
         */
        if (discoveryQueryParameters.getLocation() != null &&
                discoveryQueryParameters.getLocation().getLatitude() != 0 &&
                discoveryQueryParameters.getLocation().getLongitude() != 0){


            filteredLis = applyGeoLocationFilter(filteredLis, discoveryQueryParameters);

        }

        /*
         * Apply pagination
         */
        if ((discoveryQueryParameters.getMax() != 0) && (discoveryQueryParameters.getOffset() != 0)){

            /*
             * Apply pagination
             */
            if (filteredLis.size() > discoveryQueryParameters.getMax() &&
                    filteredLis.size() > discoveryQueryParameters.getOffset()){
                filteredLis =  filteredLis.subList(discoveryQueryParameters.getOffset(), discoveryQueryParameters.getMax());
            }else if (filteredLis.size() > 100) {
                filteredLis = filteredLis.subList(discoveryQueryParameters.getOffset(), 100);
            }

        }else if (filteredLis.size() > 100) {
            filteredLis = filteredLis.subList(0, 100);
        }

        return filteredLis;

    }

    /**
     * Method that apply geo location filter to the list
     *
     * @param listToApply
     * @return List<PlatformComponentProfile>
     */
    private List<PlatformComponentProfile> applyGeoLocationFilter(List<PlatformComponentProfile> listToApply, DiscoveryQueryParameters discoveryQueryParameters) {

        /*
         * Hold the data ordered by distance
         */
        Map<Double, PlatformComponentProfile> orderedByDistance = new TreeMap<>();

        /*
         * For each component
         */
        for (PlatformComponentProfile platformComponentProfile: listToApply) {

            /*
             * If component have a geo location
             */
            if (platformComponentProfile.getLocation() != null){

                /*
                 * Calculate the distance between the two points
                 */
                Double componentDistance = DistanceCalculator.distance(discoveryQueryParameters.getLocation(), platformComponentProfile.getLocation(), DistanceCalculator.KILOMETERS);

                /*
                 * Compare the distance
                 */
                if (componentDistance <= discoveryQueryParameters.getDistance()){

                    /*
                     * Add to the list
                     */
                    orderedByDistance.put(componentDistance, platformComponentProfile);
                }

            }

        }

        return new ArrayList<>(orderedByDistance.values());
    }

    /**
     * Filter the PlatformComponentProfiles that match with the discoveryQueryParameters that get from other component
     *
     * @param discoveryQueryParameters
     * @param clientIdentityPublicKey
     * @return List<PlatformComponentProfile>
     */
    private  List<PlatformComponentProfile> applyDiscoveryQueryParametersFromOtherComponent(DiscoveryQueryParameters discoveryQueryParameters, String clientIdentityPublicKey) {

        System.out.println("ComponentRegisteredListWebService - applyDiscoveryQueryParametersFromOtherComponent    = ");

        List<PlatformComponentProfile>  filteredListFromOtherComponentType = new ArrayList<>();

        /*
         * Get the list from the cache that match with the other componet
         */
        List<PlatformComponentProfile> otherComponentList = (List<PlatformComponentProfile>) new ArrayList<>(searchProfile(discoveryQueryParameters.getFromOtherPlatformComponentType(), discoveryQueryParameters.getFromOtherNetworkServiceType(), discoveryQueryParameters.getIdentityPublicKey())).clone();
        System.out.println("ComponentRegisteredListWebService - otherComponentList  = " + otherComponentList.size());

        /*
         * Find the other component that match with the identity
         */
        for (PlatformComponentProfile platformComponentProfile: otherComponentList) {

            if (discoveryQueryParameters.getIdentityPublicKey() != null && discoveryQueryParameters.getIdentityPublicKey() != ""){
                List<PlatformComponentProfile>  newList = searchProfileByCommunicationCloudClientIdentity(discoveryQueryParameters.getPlatformComponentType(), discoveryQueryParameters.getNetworkServiceType(), platformComponentProfile.getCommunicationCloudClientIdentity());
                filteredListFromOtherComponentType.addAll(newList);
            }

        }

        /*
         * Remove the requester from the list
         */
        Iterator<PlatformComponentProfile> iterator = filteredListFromOtherComponentType.iterator();
        while (iterator.hasNext()){

            PlatformComponentProfile platformComponentProfileRegistered = iterator.next();
            if(platformComponentProfileRegistered.getCommunicationCloudClientIdentity().equals(clientIdentityPublicKey)){
                System.out.println("ComponentRegisteredListWebService - removing ="+platformComponentProfileRegistered.getName());
                iterator.remove();
            }
        }



        System.out.println("ComponentRegisteredListWebService - filteredListFromOtherComponentType  = "+filteredListFromOtherComponentType.size());


        return filteredListFromOtherComponentType;

    }

    /**
     * Method that search the PlatformComponentProfiles tha mach with the
     * parameters
     *
     * @param platformComponentType
     * @param networkServiceType
     * @param identityPublicKey
     * @return List<PlatformComponentProfile>
     */
    private List<PlatformComponentProfile> searchProfile(PlatformComponentType platformComponentType, NetworkServiceType networkServiceType, String identityPublicKey) {

        /*
         * Prepare the list
         */
        List<PlatformComponentProfile> temporalList = null;
        List<PlatformComponentProfile>  finalFilteredList = new ArrayList<>();

         /*
         * Switch between platform component type
         */
        switch (platformComponentType){

            case COMMUNICATION_CLOUD_SERVER :
                temporalList = new ArrayList<>(wsCommunicationCloudServer.getRegisteredCommunicationsCloudServerCache().values());
                break;

            case COMMUNICATION_CLOUD_CLIENT :
                temporalList = new ArrayList<>(wsCommunicationCloudServer.getRegisteredCommunicationsCloudClientCache().values());
                break;

            case NETWORK_SERVICE :
                temporalList = new ArrayList<>(wsCommunicationCloudServer.getRegisteredNetworkServicesCache().get(networkServiceType));
                break;

            //Others
            default :
                temporalList = wsCommunicationCloudServer.getRegisteredOtherPlatformComponentProfileCache().get(platformComponentType);
                break;

        }

        /*
         * Find the component that match with the identity
         */
        for (PlatformComponentProfile platformComponentProfile: temporalList) {

            if (platformComponentProfile.getIdentityPublicKey().equals(identityPublicKey)){
                finalFilteredList.add(platformComponentProfile);
            }
        }


        return finalFilteredList;

    }


    /**
     * Method that search the PlatformComponentProfiles tha mach with the
     * parameters
     *
     * @param platformComponentType
     * @param networkServiceType
     * @param communicationCloudClientIdentity
     * @return List<PlatformComponentProfile>
     */
    private List<PlatformComponentProfile> searchProfileByCommunicationCloudClientIdentity(PlatformComponentType platformComponentType, NetworkServiceType networkServiceType, String communicationCloudClientIdentity) {

        /*
         * Prepare the list
         */
        List<PlatformComponentProfile> temporalList = null;
        List<PlatformComponentProfile>  finalFilteredList = new ArrayList<>();

         /*
         * Switch between platform component type
         */
        switch (platformComponentType){

            case COMMUNICATION_CLOUD_SERVER :
                temporalList = new ArrayList<>(wsCommunicationCloudServer.getRegisteredCommunicationsCloudServerCache().values());
                break;

            case COMMUNICATION_CLOUD_CLIENT :
                temporalList = new ArrayList<>(wsCommunicationCloudServer.getRegisteredCommunicationsCloudClientCache().values());
                break;

            case NETWORK_SERVICE :
                temporalList = new ArrayList<>(wsCommunicationCloudServer.getRegisteredNetworkServicesCache().get(networkServiceType));
                break;

            //Others
            default :
                temporalList = wsCommunicationCloudServer.getRegisteredOtherPlatformComponentProfileCache().get(platformComponentType);
                break;

        }

        /*
         * Find the component that match with the CommunicationCloudClientIdentity
         */
        for (PlatformComponentProfile platformComponentProfile: temporalList) {

            if (platformComponentProfile.getCommunicationCloudClientIdentity().equals(communicationCloudClientIdentity)){
                finalFilteredList.add(platformComponentProfile);
            }
        }

        return finalFilteredList;

    }


    /**
     * Count the number of filter to apply
     *
     * @param discoveryQueryParameters
     * @return int
     */
    private int countFilers(DiscoveryQueryParameters discoveryQueryParameters){

        int total = 0;

        if (discoveryQueryParameters.getIdentityPublicKey() != null && discoveryQueryParameters.getIdentityPublicKey() != ""){
            total += 1;
        }

        if (discoveryQueryParameters.getAlias() != null && discoveryQueryParameters.getAlias() != ""){
            total += 1;
        }

        if (discoveryQueryParameters.getName() != null && discoveryQueryParameters.getName() != ""){
            total += 1;
        }

        if (discoveryQueryParameters.getExtraData() != null && discoveryQueryParameters.getExtraData() != ""){
            total += 1;
        }

        return  total;
    }


}
