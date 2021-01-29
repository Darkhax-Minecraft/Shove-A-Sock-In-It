package net.darkhax.sasit;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.darkhax.sasit.handler.ConfigurationHandler;
import net.darkhax.sasit.io.TracingPrintStreamFilterable;
import net.darkhax.sasit.libs.LoggerUtils;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;

@Mod(modid = "sasit", name = "Stuff A Sock In It", version = "@VERSION@", acceptableRemoteVersions = "*", certificateFingerprint = "@FINGERPRINT@")
public class SASIT {

    public static Logger LOG = LogManager.getLogger("sasit");

    @EventHandler
    public void onModConstructed (FMLConstructionEvent event) {

        LOG.info("The mod has started up!");
        ConfigurationHandler.initConfig(new File("config/sasit.cfg"));
        LOG.info("Logs containing the following text may have been removed!");

        for (final String entry : ConfigurationHandler.basicFilter) {
            LOG.info(entry);
        }

        // Generic system outstream
        if (ConfigurationHandler.filterGeneric) {
            System.setOut(new TracingPrintStreamFilterable(LogManager.getLogger("STDOUT"), System.out));
            System.setErr(new TracingPrintStreamFilterable(LogManager.getLogger("STDERR"), System.err));
        }

        // Root Oracle/Java logger
        if (ConfigurationHandler.filterJava) {
            java.util.logging.Logger.getLogger("").setFilter(LoggerUtils.FILTER);
        }

        // Root Apache/Log4J logger
        if (ConfigurationHandler.filterLog4J) {
            ((org.apache.logging.log4j.core.Logger) LogManager.getRootLogger()).addFilter(LoggerUtils.FILTER);
        }

        // The forge Log4J logger, for special cases.
        if (ConfigurationHandler.filterForge) {
            LoggerUtils.filterForgeLogger();
        }

        // Other stray Log4J loggers.
        if (ConfigurationHandler.filterOthers) {
            LoggerUtils.filterOtherLog4JLoggers();
        }
    }
    
    @EventHandler
    public void onFingerprintViolation (FMLFingerprintViolationEvent event) {

        LOG.warn("Invalid fingerprint detected! The file " + event.getSource().getName() + " may have been tampered with. This version will NOT be supported by the author!");
    }
}