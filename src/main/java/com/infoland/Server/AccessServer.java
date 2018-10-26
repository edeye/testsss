package com.infoland.Server;

public interface AccessServer {

    void linkDevice();

    void disDevice();

    void openDoor();

    void setWatchingServerRuning();

    // =====================以下功能可能不会再用到了====================================//

    void setWatchingServer();

    void setSuperPassword();

    void openNumKeyboard();

    void openNumAndCardMode();

    //================================以下功能也可能不会再用到了======================//

    void addUser();

    void deleteUser();

    void deleteAllUser();

    void queryUserNum();
}
