package com.ffanaticism.intruder.serviceprovider.client;

import com.ffanaticism.intruder.serviceprovider.entity.PlatformEntity;
import com.ffanaticism.intruder.serviceprovider.exception.NotSupportedPlatformException;

public interface SongLinkClient {
    PlatformEntity getPlatform(String source) throws NotSupportedPlatformException;
}
