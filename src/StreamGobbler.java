import java.io.*;

class StreamGobbler extends Thread
{
    private InputStream is;
    private String text = "";
    private OxDoc oxdoc;
    private boolean echo;
    
    StreamGobbler(InputStream is, OxDoc oxdoc, boolean echo)
    {
        this.is = is;
        this.oxdoc = oxdoc;
        this.echo = echo;
    }
    
    public void run()
    {
        try
        {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ( (line = br.readLine()) != null) {
                text += "> " + line + "\n";
                if (echo) 
                    oxdoc.message("> " + line);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();  
        }
    }

    String getText() {
        return text;
    }

    int length() {
        return text.length();
    }
}


