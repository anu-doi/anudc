package au.edu.anu.datacommons.storage;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.BagFactory.LoadOption;
import gov.loc.repository.bagit.transformer.Completer;
import gov.loc.repository.bagit.transformer.impl.ChainingCompleter;
import gov.loc.repository.bagit.transformer.impl.UpdateCompleter;
import gov.loc.repository.bagit.writer.Writer;
import gov.loc.repository.bagit.writer.impl.FileSystemWriter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompleterTask implements Callable<Bag> {
	private static final Logger LOGGER = LoggerFactory.getLogger(CompleterTask.class);

	private BagFactory bagFactory;
	private File bagDir;
	private List<String> addUpdatePayloadFilepaths;
	private List<String> deletePayloadFilepaths;
	private List<String> addUpdatePayloadDirs;
	private List<String> deletePayloadDirs;
	private List<String> addUpdateTagFilepaths;
	private List<String> deleteTagFilepaths;
	private List<String> addUpdateTagDirs;
	private List<String> deleteTagDirs;

	public CompleterTask(BagFactory bagFactory, File bagToComplete) {
		this.bagFactory = bagFactory;
		this.bagDir = bagToComplete;
		initLimitLists();
	}

	public void addPayloadFileAddedUpdated(String filepath) {
		addUpdatePayloadFilepaths.add(filepath);
	}
	
	public void addPayloadFileDeleted(String filepath) {
		deletePayloadFilepaths.add(filepath);
	}
	
	public void setCompleteAllFiles() {
		addUpdatePayloadFilepaths = null;
		deletePayloadFilepaths = null;
		addUpdatePayloadDirs = null;
		deletePayloadDirs = null;
	}

	@Override
	public Bag call() throws Exception {
		Bag bag = null;
		try {
			bag = bagFactory.createBag(bagDir, LoadOption.BY_FILES);
			if (addUpdatePayloadFilepaths != null && !addUpdatePayloadFilepaths.isEmpty()) {
				LOGGER.debug("Files Added/updated: {}", addUpdatePayloadFilepaths.toString());
			}
			if (deletePayloadFilepaths != null && !deletePayloadFilepaths.isEmpty()) {
				LOGGER.debug("Files Deleted: {}", deletePayloadFilepaths.toString());
			}
			bag = completeBag(bag);
			bag = writeBag(bag);
		} finally {
			IOUtils.closeQuietly(bag);
		}
		return bag;
	}

	private void initLimitLists() {
		addUpdatePayloadFilepaths = new ArrayList<String>();
		deletePayloadFilepaths = new ArrayList<String>();
		addUpdatePayloadDirs = new ArrayList<String>();
		deletePayloadDirs = new ArrayList<String>();
		addUpdateTagFilepaths = null;
		deleteTagFilepaths = null;
		addUpdateTagDirs = null;
		deleteTagDirs = null;
	}
	
	private Bag completeBag(Bag bag) {
		LOGGER.debug("Completing bag at {}...", this.bagDir.getAbsolutePath());
		Completer completer = getCompleter();
		bag = bag.makeComplete(completer);
		LOGGER.debug("Completed bag at {}", this.bagDir.getAbsolutePath());
		return bag;
	}

	private Completer getCompleter() {
		return new ChainingCompleter(createPreservationCompleter(), createDcStorageCompleter(), createUpdateCompleter());
	}

	private Completer createPreservationCompleter() {
		PreservationCompleter pc = new PreservationCompleter();
		pc.setLimitAddUpdatePayloadFilepaths(this.addUpdatePayloadFilepaths);
		pc.setLimitDeletePayloadFilepaths(this.deletePayloadFilepaths);
		return pc;
	}

	private Completer createUpdateCompleter() {
		UpdateCompleter updateCompleter = new UpdateCompleter(this.bagFactory);
		
		updateCompleter.setLimitAddPayloadFilepaths(this.addUpdatePayloadFilepaths);
		updateCompleter.setLimitUpdatePayloadFilepaths(this.addUpdatePayloadFilepaths);
		updateCompleter.setLimitDeletePayloadFilepaths(this.deletePayloadFilepaths);
		
		updateCompleter.setLimitAddPayloadDirectories(this.addUpdatePayloadDirs);
		updateCompleter.setLimitUpdatePayloadDirectories(this.addUpdatePayloadDirs);
		updateCompleter.setLimitDeletePayloadDirectories(this.deletePayloadDirs);
		
		updateCompleter.setLimitAddTagFilepaths(this.addUpdateTagFilepaths);
		updateCompleter.setLimitUpdateTagFilepaths(this.addUpdateTagFilepaths);
		updateCompleter.setLimitDeleteTagFilepaths(this.deleteTagFilepaths);

		updateCompleter.setLimitAddTagDirectories(this.addUpdateTagDirs);
		updateCompleter.setLimitUpdateTagDirectories(this.addUpdateTagDirs);
		updateCompleter.setLimitDeleteTagDirectories(this.deleteTagDirs);
		
		return updateCompleter;
	}

	private Completer createDcStorageCompleter() {
		DcStorageCompleter dcStorageCompleter = new DcStorageCompleter();
		
		dcStorageCompleter.setLimitAddUpdatePayloadFilepaths(this.addUpdatePayloadFilepaths);
		dcStorageCompleter.setLimitDeletePayloadFilepaths(this.deletePayloadFilepaths);
		
		return dcStorageCompleter;
	}

	private Bag writeBag(Bag bag) {
		LOGGER.debug("Writing bag at {}...", this.bagDir.getAbsolutePath());
		Writer writer = createWriter();
		bag = writer.write(bag, bagDir);
		LOGGER.debug("Finished writing bag at {}", this.bagDir.getAbsolutePath());
		return bag;
	}

	private Writer createWriter() {
		FileSystemWriter writer = new FileSystemWriter(bagFactory);
		writer.setTagFilesOnly(true);
		return writer;
	}
}
