package com.ericsson.addroneapplication.communication.data;

/**
 * Created by ebarnaw on 2016-10-14.
 */
public class RouteContainer implements SignalPayloadData {

    public RouteContainer(final byte[] dataArray) {

    }

    @Override
    public SignalData.Command getDataType() {
        return SignalData.Command.ROUTE_CONTAINER_DATA;
    }
}
