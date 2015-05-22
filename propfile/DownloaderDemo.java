import java.io.* ;
import java.util.* ;
import java.net.URL;

public class DownloaderDemo
{
	public static void main( String[] args )
	{
		if( args.length == 0 )
		{
			System.out.println("Usage: java DownloaderDemo URL FILENAME") ;
			return ;
		}
		System.out.println( ">>> "+args[0]+" --> "+args[1] );
		Downloader.download( args[0] , args[1] , "" ) ;
		
	}
}
