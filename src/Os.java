public class Os {

  enum OperatingSystem { Win32, Linux, Solaris, Mac, Unknown };

  private Os() {
  }

  public static String getOsName() {
    return System.getProperty("os.name", "unknown");
  }
  
  public static OperatingSystem getOperatingSystem() {
    String osname = System.getProperty("os.name", "generic").toLowerCase();
    if (osname.startsWith("windows"))
      return OperatingSystem.Win32;
    else if (osname.startsWith("linux"))
      return OperatingSystem.Linux;
    else if (osname.startsWith("mac") || osname.startsWith("darwin"))
      return OperatingSystem.Mac;
    else if (osname.startsWith("sunos"))
      return OperatingSystem.Solaris;
    else 
      return OperatingSystem.Unknown;
  }

}


