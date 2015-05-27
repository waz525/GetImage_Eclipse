
import java.io.* ;
import java.net.* ;
import java.util.* ;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseHtml {
    
    private String content = null ;

    public String getHtmlCode(String httpUrl , String encodeType) {
        String htmlCode = "";
        try {
            URL url = new java.net.URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection = (HttpURLConnection) url.openConnection();
	    //connection.setRequestProperty("Accept" , "image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/x-shockwave-flash, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*" ) ;
	    //connection.setRequestProperty("Accept-Language" , "zh-cn") ;
	    //connection.setRequestProperty("Accept-Encoding" , "gzip, deflate") ;
            connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
            
            connection.setConnectTimeout(60000);
            connection.setReadTimeout(180000);
            
            connection.connect();
            //InputStreamReader isr = new InputStreamReader(connection.getInputStream());   
            //BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(),"GB2312"));
            InputStreamReader isr = new InputStreamReader(connection.getInputStream(),encodeType);   
            BufferedReader in = new BufferedReader(isr);
            String inputLine;   
            StringBuffer tempHtml = new StringBuffer();   
            while((inputLine = in.readLine()) != null) 
            {   
                tempHtml.append(inputLine+"\n");   
            }
            htmlCode = tempHtml.toString() ;
        } catch (Exception e) {
        }
        if(htmlCode == null){
            return "";
        }
        return htmlCode;
    }

    public String getHtmlCode(String httpUrl , String encodeType ,String cookies) {
        String htmlCode = "";
        try {
            URL url = new java.net.URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Cookie",cookies);
            connection.setConnectTimeout(60000);
            connection.setReadTimeout(180000);
            
            connection.connect(); 
	    
            InputStreamReader isr = new InputStreamReader(connection.getInputStream(),encodeType);   
            BufferedReader in = new BufferedReader(isr);
            String inputLine;   
            StringBuffer tempHtml = new StringBuffer();   
            while((inputLine = in.readLine()) != null) 
            {   
                tempHtml.append(inputLine+"\n");   
            }
            htmlCode = tempHtml.toString() ;
        } catch (Exception e) {
        }
        if(htmlCode == null){
            return "";
        }
        return htmlCode;
    }

    @SuppressWarnings("unused")
	public String getHtmlCode(String httpUrl , String encodeType , String login_param , String url_login ) {
	String sessionId = "" ;
        String htmlCode = "";
	try {
            PrintWriter out = null;
            BufferedReader in = null;
            String key=null;
            URL loginUrl = new URL(url_login);
            URLConnection conn = loginUrl.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            out = new PrintWriter(conn.getOutputStream());
            out.print(login_param);
            out.flush();
            String cookieVal = null;
            for (int i = 1; (key = conn.getHeaderFieldKey(i)) != null; i++ ) {
                    if (key.equalsIgnoreCase("set-cookie")) {
                        cookieVal = conn.getHeaderField(i);
                        cookieVal = cookieVal.substring(0, cookieVal.indexOf(";"));
                        sessionId = sessionId+cookieVal+";";
                    }
            }
            conn.setConnectTimeout(60000);
            conn.setReadTimeout(180000);
            
            conn.connect(); 
            InputStream ins = conn.getInputStream();   
	}
        catch(Exception e)
        {
            e.printStackTrace();
        }
//        System.out.println("---"+sessionId+"---");
        try {
            URL url = new java.net.URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Cookie",sessionId);
            connection.connect();
            InputStreamReader isr = new InputStreamReader(connection.getInputStream(),encodeType);   
            BufferedReader in = new BufferedReader(isr);
            String inputLine;   
            StringBuffer tempHtml = new StringBuffer();   
            while((inputLine = in.readLine()) != null) 
            {   
                tempHtml.append(inputLine+"\n");   
            }
            htmlCode = tempHtml.toString() ;
        } catch (Exception e) {
        }
        if(htmlCode == null){
            return "";
        }
        return htmlCode;
    }

    @SuppressWarnings("unused")
	public String getCookie(String url_login , String login_param ) {
        String sessionId = "" ;
        try {
            PrintWriter out = null;
            BufferedReader in = null;
            String key=null;
            URL loginUrl = new URL(url_login);
            URLConnection conn = loginUrl.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            out = new PrintWriter(conn.getOutputStream());
            out.print(login_param);
            out.flush();
            String cookieVal = null;
            for (int i = 1; (key = conn.getHeaderFieldKey(i)) != null; i++ ) {
                    if (key.equalsIgnoreCase("set-cookie")) {
                        cookieVal = conn.getHeaderField(i);
                        cookieVal = cookieVal.substring(0, cookieVal.indexOf(";"));
                        sessionId = sessionId+cookieVal+";";
                    }
            }
            conn.connect();
            InputStream ins = conn.getInputStream();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
	return sessionId ;
    }

    public String getContent( String url , String encodeType){
        content = getHtmlCode(url,encodeType);
        return content ;
    }

    public String getContent( String url , String encodeType , String cookie ){
        content = getHtmlCode(url,encodeType,cookie);
        return content ;
    }

    public String getContent( String url , String encodeType ,String login_param , String login_url ){
        content = getHtmlCode(url,encodeType,login_param,login_url);
        return content ;
    }
    public boolean isContain( String text ){
        try {
        String tmp = new String( content ) ;
        if( tmp.indexOf( text ) != -1 )
            return true ;
        }catch( Exception e )
        {
            e.printStackTrace();
        }
        return false ;
    }

    public void printContent(){
	System.out.println( content ) ;
    }

    public String[] getHtmlUrl( String url , String encodeType){
	ArrayList<String> list = new ArrayList<String>() ;
        String searchHtmlReg = "(?x)href=('|\")*((http://([\\w-]+\\.)+[\\w-]+(:[0-9]+)*)*(/)*([\\w-]+/)*([\\w-]+\\.(html|htm)))('|\")*";
        String content = getHtmlCode(url,encodeType);
        Pattern pattern = Pattern.compile(searchHtmlReg);
        Matcher matcher = pattern.matcher(content);
        while(matcher.find()){
            list.add(matcher.group(2));
        }
	String[] urls = ( String[] ) list.toArray( new String[0] ) ;
	return urls ;
    }

    public String[] getImgUrl( String url , String encodeType){
        ArrayList<String> list = new ArrayList<String>() ;
	String searchImgReg = "(?x)(src|SRC)=('|\")*((http://([\\w-]+\\.)+[\\w-]+(:[0-9]+)*)*(/)*([\\w-]+/)*([\\w-]+\\.(jpg|JPG|png|PNG)))('|\")";
        String content = getHtmlCode(url,encodeType);
        Pattern pattern = Pattern.compile(searchImgReg);
        Matcher matcher = pattern.matcher(content);
        while(matcher.find()){
            list.add(matcher.group(2));
        }
        String[] urls = ( String[] ) list.toArray( new String[0] ) ;
        return urls ;
    }

    public String[] getHtmlUrl(){
        ArrayList<String> list = new ArrayList<String>() ;
        String searchHtmlReg = "(?x)href=('|\")*((http://([\\w-]+\\.)+[\\w-]+(:[0-9]+)*)*(/)*([\\w-]+/)*([\\w-]+\\.(html|htm)))('|\")*";
	if( this.content == null )
               return null ;
        Pattern pattern = Pattern.compile(searchHtmlReg);
        Matcher matcher = pattern.matcher(content);
        while(matcher.find()){
            list.add(matcher.group(2));
        }
        String[] urls = ( String[] ) list.toArray( new String[0] ) ;
        return urls ;
    }

    public String[] getImgUrl(){
        ArrayList<String> list = new ArrayList<String>() ;
	String searchImgReg = "(?x)(src|SRC)=('|\")*((http://([\\w-]+\\.)+[\\w-]+(:[0-9]+)*)*(/)*([\\w-]+/)*([\\w-]+\\.(jpg|JPG|png|PNG)))('|\")*";
	if( this.content == null )
	       return null ;
        Pattern pattern = Pattern.compile(searchImgReg);
        Matcher matcher = pattern.matcher(content);
        while(matcher.find()){
            list.add(matcher.group(3));
        }
        String[] urls = ( String[] ) list.toArray( new String[0] ) ;
        return urls ;
    }
    
    public String[] getUrlByReg(String searchReg , int sectid){
        ArrayList<String> list = new ArrayList<String>() ;
        if( this.content == null )
               return null ;
        Pattern pattern = Pattern.compile(searchReg);
        Matcher matcher = pattern.matcher(content);
        while(matcher.find()){
            list.add(matcher.group(sectid));
        }
        String[] urls = ( String[] ) list.toArray( new String[0] ) ;
        return urls ;
    }

    public String[] getUrlByReg( String url , String encodeType , String searchReg , int sectid ){
        ArrayList<String> list = new ArrayList<String>() ;
        String content = getHtmlCode(url,encodeType);
        Pattern pattern = Pattern.compile(searchReg);
        Matcher matcher = pattern.matcher(content);
        while(matcher.find()){
            list.add(matcher.group(sectid));
        }
        String[] urls = ( String[] ) list.toArray( new String[0] ) ;
        return urls ;
    }

    public String[] getAllRes(){
	String searchHtmlReg = "(?x)src=('|\")*((http://([\\w-]+\\.)+[\\w-]+(:[0-9]+)*)*(/)*([\\w-]+/)*([\\w-]+)(\\.[\\w-]+))('|\")*(?x)";
	return getUrlByReg( searchHtmlReg , 2 ) ;
    }
	

    public String getTitle()
    {
        if( this.content == null )
               return "" ;
	String str = "" ;
        int start = -1 ;
        int end = -1 ;
        start = content.indexOf("<title>") ;
        end = content.indexOf("</title>") ;
	if( start == -1 && end == -1 )
	{
		start = content.indexOf("<TITLE>") ;
		end = content.indexOf("</TITLE>") ;
	}

        if( start > -1 && end > -1 && end > start )
        {
               str   =   content.substring(start+7,end) ;
        }
	return str ;
    }

    public String getTitle(String url , String encodeType)
    {
	getContent(url,encodeType) ;
	return getTitle() ;
    }


}

