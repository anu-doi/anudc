package au.edu.anu.datacommons.storage;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.BagFactory.LoadOption;
import gov.loc.repository.bagit.transformer.Completer;
import gov.loc.repository.bagit.transformer.impl.ChainingCompleter;
import gov.loc.repository.bagit.transformer.impl.UpdateCompleter;
import gov.loc.repository.bagit.writer.Writer;
import gov.loc.repository.bagit.writer.impl.FileSystemWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompleterTask implements Callable<Bag> {
	private static final Logger LOGGER = LoggerFactory.getLogger(DcStorageCompleter.class);

	private BagFactory bagFactory;
	private Bag bag;
	private List<String> addUpdatePayloadFilepaths;
	private List<String> deletePayloadFilepaths;
	private List<String> addUpdatePayloadDirs;
	private List<String> deletePayloadDirs;
	private List<String> addUpdateTagFilepaths;
	private List<String> deleteTagFilepaths;
	private List<String> addUpdateTagDirs;
	private List<String> deleteTagDirs;

	public CompleterTask(BagFactory bagFactory, Bag bagToComplete) {
		this.bagFactory = bagFactory;
		this.bag = bagToComplete;
		initLimitLists();
	}

	public void addPayloadFileAddedUpdated(String filepath) {
		addUpdatePayloadFilepaths.add(filepath);
	}
	
	public void addPayloadFileDeleted(String filepath) {
		deletePayloadFilepaths.add(filepath);
	}

	@Override
	public Bag call() throws Exception {
		bag = bagFactory.createBag(bag.getFile(), LoadOption.BY_FILES);
		LOGGER.debug("Completing bag at {}", this.bag.getFile().getAbsolutePath());
		if (addUpdatePayloadFilepaths != null && !addUpdatePayloadFilepaths.isEmpty()) {
			LOGGER.debug("Files Added/updated: {}", addUpdatePayloadFilepaths.toString());
		}
		if (deletePayloadFilepaths != null && !deletePayloadFilepaths.isEmpty()) {
			LOGGER.debug("Files Deleted: {}", deletePayloadFilepaths.toString());
		}
		completeBag();
		writeBag();
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
	
	private void completeBag() {
		Completer completer = getCompleter();
		this.bag = bag.makeComplete(completer); 
	}

	private Completer getCompleter() {
		return new ChainingCompleter(createDcStorageCompleter(), createUpdateCompleter());
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

	private void writeBag() {
		Writer writer = createWriter();
		bag = writer.write(bag, bag.getFile());
	}

	private Writer createWriter() {
		FileSystemWriter writer = new FileSystemWriter(bagFactory);
		writer.setTagFilesOnly(true);
		return writer;
	}
}
