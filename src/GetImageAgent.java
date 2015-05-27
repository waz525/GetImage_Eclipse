
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
		int sPort = 999 ;
		if( args.length > 0 && ( args[0].equals("help") || args[0].equals("-h")) )
		{
			System.out.println("Version: 4.2" ) ;
			System.out.println("Usage: java GetImageAgent [Action [Paramet]]\n") ;
			System.out.println(" e.g.: java GetImageAgent") ;
			System.out.println("       java GetImageAgent help") ;
			System.out.println("       java GetImageAgent LoadProp GetImage.prop") ;
			System.out.println("       java GetImageAgent WriteProp GetImage.prop") ;
			System.out.println("       java GetImageAgent StartRun GetImage.prop") ;
			System.out.println("       java GetImageAgent Port 999") ;
			return ;
		}
		
		GetImage gi =  new GetImage() ;
		if( args.length > 0 && args[0].equals("WriteProp") )
		{
			if(args.length < 2) {
				System.out.println("[Error] no prop file name !\n        AS : java GetImageAgent WriteProp GetImage.prop ") ;
				return ;
			}
			gi.SaveDefultPropFile(args[1]) ;
			return ;
		}
		
		if( args.length > 0 && args[0].equals("Port") ) {
			if(args.length < 2) {
				System.out.println("[Error] no Port Number !\n        AS : java GetImageAgent Port 999 ") ;
				return ;				
			}
			sPort = Integer.parseInt(args[1]);
		}
		
		MBeanServer mbs = MBeanServerFactory.createMBeanServer() ;

		ObjectName getImageObject = new ObjectName( "MYCLASS:name=GetImage" ) ;
		mbs.registerMBean( gi , getImageObject ) ;
		
		HtmlAdaptorServer htmlAdaptor = new HtmlAdaptorServer() ;
		ObjectName adaptorON = new ObjectName( "HtmlAdaptorServer:name=GetImageAgent" ) ;
		mbs.registerMBean( htmlAdaptor , adaptorON ) ;
		htmlAdaptor.setPort(sPort) ;
		
		System.out.println("Starting GetImageAgent Server (PORT:"+sPort+")... " ) ;
		
		htmlAdaptor.start() ;
		
		
		if( args.length > 0 )
		{
			if(args.length < 2) 
			{
				System.out.println("[Error] no prop file name !\n") ;
			}
			if(args[0].equals("LoadProp") ) gi.LoadPropfile(args[1]) ;
			if(args[0].equals("StartRun") ) System.out.println(gi.toStartGetImage() );
		}
	}
}
