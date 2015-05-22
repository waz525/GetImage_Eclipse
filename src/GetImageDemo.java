
public class GetImageDemo 
{
	private void printHelp()
	{
		System.out.println("Version: 3.3" ) ;
		System.out.println("Usage: java GetImageDemo [propFileName] [-h] [-p propFileName]") ;
		System.out.println("       propFileName: getImage from propFileName ") ;
		System.out.println("       -p propFileName: produce propFile by propFileName " ) ;
	}
	
	public static void main( String[] args )
	{
		String fileName = "./GetImage.prop" ;
		GetImage a = new GetImage() ;
		GetImageDemo b = new GetImageDemo() ;
		if( args.length > 0 )
		{
			if( args[0].equals("-h") )
			{
				b.printHelp() ;
				return ;
			}
			if( args[0].equals("-p") )
			{
				if( args.length != 2 )
				{
					b.printHelp() ;
					return ;
				}
				a.SaveDefultPropFile(args[1]) ;
				return ;
			}
			fileName=args[0] ;
		}

		a.LoadPropfile(fileName) ;
		System.out.println(a.toStartGetImage() );

	}
}
