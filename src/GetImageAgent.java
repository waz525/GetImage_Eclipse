
import javax.management.* ;
import com.sun.jdmk.comm.HtmlAdaptorServer ;

public class GetImageAgent 
{
	public static void main( String[] args ) 
		throws MalformedObjectNameException, 
			NullPointerException, 
			InstanceAlreadyExistsException, 
			MBeanRegistrationException, 
			NotCompliantMBeanException
	{
		if( args.length > 0 && ( args[0].equals("help") || args[0].equals("-h")) )
		{
			System.out.println("Version: 4.1" ) ;
			System.out.println("Usage: java GetImageAgent [Action [Paramet]]\n") ;
			System.out.println(" e.g.: java GetImageAgent") ;
			System.out.println("       java GetImageAgent help") ;
			System.out.println("       java GetImageAgent LoadProp GetImage.prop") ;
			System.out.println("       java GetImageAgent WriteProp GetImage.prop") ;
			System.out.println("       java GetImageAgent StartRun GetImage.prop") ;
			return ;
		}
		
		GetImage gi =  new GetImage() ;
		if( args.length > 0 && args[0].equals("WriteProp") )
		{
			if(args.length < 2) 
			{
				System.out.println("[Error] no prop file name !\n        AS : java GetImageAgent WriteProp GetImage.prop ") ;
				return ;
			}
			gi.SaveDefultPropFile(args[1]) ;
			return ;
		}
		
		MBeanServer mbs = MBeanServerFactory.createMBeanServer() ;

		ObjectName getImageObject = new ObjectName( "MYCLASS:name=GetImage" ) ;
		mbs.registerMBean( gi , getImageObject ) ;
		
		HtmlAdaptorServer htmlAdaptor = new HtmlAdaptorServer() ;
		ObjectName adaptorON = new ObjectName( "HtmlAdaptorServer:name=GetImageAgent" ) ;
		mbs.registerMBean( htmlAdaptor , adaptorON ) ;
		htmlAdaptor.setPort(999) ;
		
		System.out.println("Starting GetImageAgent Server (PORT:999)... " ) ;
		
		htmlAdaptor.start() ;
		
		
		if( args.length > 0 )
		{
			if(args.length < 2) 
			{
				System.out.println("[Error] no prop file name !\n") ;
			}
			gi.LoadPropfile(args[1]) ;
			if(args[0].equals("StartRun") )System.out.println(gi.toStartGetImage() );
		}
	}
}
