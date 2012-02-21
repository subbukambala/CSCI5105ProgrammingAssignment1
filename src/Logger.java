import java.util.logging.FileHandler;
import java.util.logging.StreamHandler;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;

/**
 * @authors Bala Subrahmanyam Kambala, Daniel William DaCosta
 * @license GPLv3 (http://www.gnu.org/copyleft/gpl.html)
 * @descriptrion Logging Facilities.
 */

public class Logger 
{
    /**
     * @todo Everything
     */
    private java.util.logging.Logger logger;

    /**
     * @todo Everything
     */
    Logger(String subsys, String filename)
    {
	//fh = new FileHandler("c:\\MyLogFile.log", true);


    }

    /**
     * @todo Everything
     * @description Log to standard out.
     */
    Logger(String subsys)
    {
	logger = java.util.logging.Logger.getLogger(subsys);
	logger.setLevel(Level.FINER);
	Handler ch = new ConsoleHandler();
	ch.setLevel(Level.FINER);

	//SimpleFormatter formatter = new SimpleFormatter();
	//StreamHandler sh = new StreamHandler(System.out, formatter);
	//sh.setLevel(Level.ALL);
	logger.addHandler(ch);
    }

    /**
     * @todo Everything
     */
    void log(Level lvl,String msg)
    {
	logger.log(lvl,msg);
    }
}