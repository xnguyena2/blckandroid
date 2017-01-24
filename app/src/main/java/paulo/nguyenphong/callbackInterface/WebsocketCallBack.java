package paulo.nguyenphong.callbackInterface;

/**
 * Created by Nguyen Phong on 11/13/2016.
 */

public interface WebsocketCallBack {
    public void onRecivedMsg(String msg);
    public void onError(Exception ex);
    public void onClose(int code, String reason, boolean remote);
    public void onOpen();
}
