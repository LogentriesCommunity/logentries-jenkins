package com.logentries.jenkins;

import java.io.IOException;
import java.io.OutputStream;
import java.net.UnknownHostException;

import org.kohsuke.stapler.DataBoundConstructor;

import com.logentries.jenkins.Messages;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;

public class LogentriesBuildWrapper extends BuildWrapper {

	private final String token;

	/**
	 * Create a new {@link LogentriesBuildWrapper}.
	 */
	@DataBoundConstructor
	public LogentriesBuildWrapper(String token) {
		this.token = token;
	}
	
	public String getToken() {
		return token;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public OutputStream decorateLogger(AbstractBuild build, OutputStream logger) {
		try {
			return new LogentriesLogDecorator(logger, this.token);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return logger;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Environment setUp(AbstractBuild build, Launcher launcher,
			BuildListener listener) throws IOException, InterruptedException {
		return new Environment() {
		};
	}

    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

	/**
	 * Registers {@link AnsiColorBuildWrapper} as a {@link BuildWrapper}.
	 */
	@Extension
	public static final class DescriptorImpl extends BuildWrapperDescriptor {

		public DescriptorImpl() {
			super(LogentriesBuildWrapper.class);
			load();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getDisplayName() {
			return Messages.DisplayName();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isApplicable(AbstractProject<?, ?> item) {
			return true;
		}
	}
}
