
import java.io.FileOutputStream;       
import java.io.InputStream;       
import java.io.OutputStream;       
import java.net.URL;       
import java.net.URLConnection;       
import java.io.* ; 

public class Downloader {       


  public static void download(String urlString, String fileName, String cookie) {   
  	try{    
	         
	    URL url = new URL(urlString);       
	          
	    URLConnection con = url.openConnection(); 
            con.setRequestProperty("Accept" , "*/*" ) ;
            con.setRequestProperty("User-Agent" , "Wget/1.10.2 (Red Hat modified)");
 	    if( cookie != "" ) con.setRequestProperty("Cookie",cookie) ;      
            con.setConnectTimeout(60000);
            con.setReadTimeout(180000);
	         
	    InputStream is = con.getInputStream();       
	    int fileLength = con.getContentLength();  
	        
	    byte[] bs = new byte[1024];       
	       
	    int len; 
	    String filename = fileName.replaceAll(" ","_") ;
//	    String filename = fileName ;
	    int index = filename.lastIndexOf("/") ;
	    File file = new File( filename.substring( 0,index+1) );
	    if( ! file.exists() )
	    {
		int ind = filename.indexOf("/") ;
		while( ind != index )
		{
//			System.out.println( "HAHA---> "+filename.substring( 0 , ind ) ) ;
			file=new File( filename.substring( 0 , ind ) );
			if( !file.exists() ) file.mkdir() ;
			ind = filename.indexOf("/", ind+1) ;
		}
		file = new File( filename.substring( 0,index+1) );
		file.mkdir() ;
	    }

	    Log.writeLog( "./download.log" , urlString+" ---> "+filename+"("+fileLength+")");

	    OutputStream os = new FileOutputStream(filename);       
	        
	    while ((len = is.read(bs)) != -1) {       
	      os.write(bs, 0, len);       
	    }       
	      
	    os.close();       
	    is.close(); 
	  }catch( Exception e ) {
		Log.writeLog( "./download_err.log" , urlString+" ---> "+fileName.replaceAll(" ","_") );
		Log.removeFile( fileName.replaceAll(" ","_") ) ;
//	  	e.printStackTrace();
	  }
    
  }       
}
