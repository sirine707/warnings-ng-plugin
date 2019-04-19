package io.jenkins.plugins.analysis.warnings;

import edu.hm.hafner.analysis.parser.MentorParser;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import io.jenkins.plugins.analysis.core.model.ReportScanningTool;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Provides a parser and customized messages for the Cadence Incisive Enterprise Simulator.
 *
 * @author Derrick Gibelyou
 */
public class MentorGraphics extends ReportScanningTool {
    private static final long serialVersionUID = 8284958840616127492L;
    private static final String ID = "cadence";

    /** Creates a new instance of {@link MentorGraphics}. */
    @DataBoundConstructor
    public MentorGraphics() {
        super();
        // empty constructor required for stapler
    }

    @Override
    public MentorParser createParser() {
        return new MentorParser();
    }

    /** Descriptor for this static analysis tool. */
    @Symbol("mentor")
    @Extension
    public static class Descriptor extends ReportScanningToolDescriptor {
        /**
         * Creates a new instance of {@link Descriptor}.
         */
        public Descriptor() {
            super(ID);
        }

        @NonNull
        @Override
        public String getDisplayName() {
            return Messages.Warnings_MentorGraphics_ParserName();
        }
    }
}
