import java.io.* ;
import java.util.* ;
import java.net.URL;

public class HtmlDemo
{
	public static void main( String[] args )
	{
		if( args.length == 0 )
		{
			System.out.println("Usage: java HtmlDemo URLSTR EncodeType FileName") ;
			return ;
		}
		try
		{
			ParseHtml ph = new ParseHtml() ;
			String content = ph.getContent( args[0] , args[1] ) ;
			String title = ph.getTitle() ;
			System.out.println("---> "+title+" <---") ;
			File outFile = new File( args[2] ) ;
			FileWriter fwstr = new FileWriter( outFile ) ;
			fwstr.write( content ) ;
			fwstr.close();
			String[] ImgUrl = ph.getAllRes() ;
			for( int i = 0 ; i < ImgUrl.length ; i++ )
			{
				System.out.println("==> "+i+" : "+ImgUrl[i] ) ;
			}
			
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
	}
}
