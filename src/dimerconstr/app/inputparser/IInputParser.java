package dimerconstr.app.inputparser;

import java.io.IOException;

import dimerconstr.app.jobs.IJobDescriptor;
import dimerconstr.containers.atom.InvalidAtomParametersException;

public interface IInputParser {
	//Parse file
	public IJobDescriptor parse(String filename) throws IOException, 
														InvalidInputFileStructureException,
														InvalidAtomParametersException;
}
