package TroChoiBaiTienLen.model.notification;

public class ServerNotification {
    
    private String notific;
    private boolean isError = false;
    
    public ServerNotification(){
        
    }
    
    public void setNotific(String notific){
        this.notific = notific;
    }
    
    public String getNotific(){
        return this.notific;
    }
    
    public void setIsError(boolean isError){
        this.isError = isError;
    }
    
    public boolean getIsError(){
        return this.isError;
    }
}
