package dimerconstr.constructor.aux;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import dimerconstr.app.jobs.IJobDescriptor;
import dimerconstr.aux.math.Vector3d;
import dimerconstr.aux.periodicTableOfElements.IUPACPeriodicTableOfElements;
import dimerconstr.containers.atom.IAtom;
import dimerconstr.containers.bond.IHBond;
import dimerconstr.containers.molecule.DimerComplex;
import dimerconstr.containers.molecule.IDimerComplex;
import dimerconstr.containers.molecule.IMolecule;
import dimerconstr.log.ILogger;
import dimerconstr.log.LoggerThread;
import dimerconstr.storage.IStorage;
import dimerconstr.storage.StorageThread;

public class Dimer extends Thread implements IDimer {
	/*
	 *	Properties
	 */
	//DEBUG
	protected boolean isDebugFileCreated = false;
	protected long count = 0;
	//Stationary monomer
	protected IMolecule mS = null;
	//Moveable monomer
	protected IMolecule mM = null;
	//Job name
	protected String jobName = null;
	//H-bonds
	protected List<IHBond> bonds = null;
	protected IHBond[] bond = new IHBond[2];
	//Constructed complexes
	protected Map<String, List<IMolecule>> complexes = null;
	//Storage
	IStorage storage = StorageThread.getInstance();
	//Logger
	ILogger logger = LoggerThread.getInstance();
	
	/*
	 *	In-class constants
	 */
	//DEBUG constants
	protected final String DEBUG_FILE = "debug.txt";
	//Step constants
	//Everywhere applies principle of fine adjustment except for dKSI which has been set to have 3 positions per range
	protected final double dR = 0.05d;
	protected final double dKhi = Math.toRadians(10.0d);
	protected final double dDelta = Math.toRadians(2.0d);
	protected final double dKappa = Math.toRadians(2.0d);
	protected final double dZeta = Math.toRadians(2.0d);
	protected final double dTheta = Math.toRadians(2.0d);
	//Valid angles, radians
	protected final double ANGLE_PLANAR_MIN = Math.toRadians(-10.0d);
	protected final double ANGLE_PLANAR_MID = Math.toRadians(0.0d);
	protected final double ANGLE_PLANAR_MAX = Math.toRadians(10.0d);
	protected final double ANGLE_NORMAL_MIN = Math.toRadians(80.0d);
	protected final double ANGLE_NORMAL_MID = Math.toRadians(90.0d);
	protected final double ANGLE_NORMAL_MAX = Math.toRadians(100.0d);
	protected final double ANGLE_NORMAL_MMIN = Math.toRadians(-100.0d);
	protected final double ANGLE_NORMAL_MMID = Math.toRadians(-90.0d);
	protected final double ANGLE_NORMAL_MMAX = Math.toRadians(-80.0d);
	//Angle ranges
	protected final double DELTA_MAX = 2.0d * Math.PI;
	protected final double KAPPA_MAX = 2.0d * Math.PI;
	protected final double KAPPA_MAX_NORMAL = Math.PI;
	protected final double ZETA_MIN = Math.toRadians(-90.0d);
	protected final double ZETA_PLANAR = 0.0d;
	protected final double ZETA_MAX = Math.toRadians(90.0d);
	protected final double THETA_MIN = Math.toRadians(0.0d);
	protected final double THETA_PLANAR = Math.toRadians(0.0d);
	protected final double THETA_MAX = Math.toRadians(360.0d);
	
	/*
	 *	//FIXME Debug 
	 */
	public void DEBUGprintMs(boolean name) {
		try {
			FileWriter file = new FileWriter(mS.getName());
			Iterator<IAtom> atoms = mS.getAtoms();
			if(name) {
				file.write(mS.getName() + "\n");
			}
			while(atoms.hasNext())
			{
				IAtom atom = atoms.next();
				Vector3d pos = atom.getPosition();
				file.write(String.format("%s\t%.6f\t%.6f\t%.6f\n", atom.getName(), pos.x, pos.y, pos.z));
			}
			file.close();
		} catch (IOException e) { e.printStackTrace(); }
	}
	public void DEBUGprintMm(boolean name) {
		try {
			FileWriter file = new FileWriter(mM.getName());
			Iterator<IAtom> atoms = mM.getAtoms();
			if(name) {
				file.write(mM.getName() + "\n");
			}
			while(atoms.hasNext())
			{
				IAtom atom = atoms.next();
				Vector3d pos = atom.getPosition();
				file.write(String.format("%s\t%.6f\t%.6f\t%.6f\n", atom.getName(), pos.x, pos.y, pos.z));
			}
			file.close();
		} catch (IOException e) { e.printStackTrace(); }
	}
	public void DEBUGprintComplex(boolean isMirrored) {
		try {
			String format = "%s\t%.6f\t%.6f\t%.6f\n";
			String filename = DEBUG_FILE;
			String name = String.format("%d_%.2f_%.2f_%.2f_%.2f_%s\n",
										++count,
										Math.toDegrees(mM.getPlaneAngle()), 
										Math.toDegrees(mM.getDiskAngle()),
										Math.toDegrees(mM.getRotationAngle()),
										mM.getRadiusVector(),
										isMirrored);
			FileWriter file = new FileWriter(filename, isDebugFileCreated);
			if(!isDebugFileCreated) isDebugFileCreated = true;
			file.write(name);
			//Ms atoms
			Iterator<IAtom> atoms = mS.getAtoms();
			while(atoms.hasNext())
			{
				IAtom atom = atoms.next();
				Vector3d pos = atom.getPosition();
				file.write(String.format(format, atom.getName(), pos.x, pos.y, pos.z));
			}
			//Mm atoms
			atoms = mM.getAtoms();
			while(atoms.hasNext())
			{
				IAtom atom = atoms.next();
				Vector3d pos = atom.getPosition();
				file.write(String.format(format, atom.getName(), pos.x, pos.y, pos.z));
			}
			file.close();
		} catch (IOException e) { e.printStackTrace(); }
	}
	
	/*
	 *	Interface
	 */
	//Thread
	@Override
	public void run() {
		//Log job name
		logger.log(String.format("(TID: %d) Processing job: %s", super.getId(), jobName));
		construct();
	}
	
	//IDimer
	//Construct dimer
	@Override
	public void construct() {
		//Angles between molecular planes
		//[-100; 80] U [-10; 10] U [80; 100]
		//Set Ms on plane XY
		mS.resetOrientation();
		//Set Mm on plane XY
		mM.resetOrientation();
		//Set angle between Mm and Ms KSI=-100
		mM.setPlaneAngle(ANGLE_NORMAL_MMIN);
		//Set filter type to NORMAL-
		EComplexType filterType = EComplexType.NORMAL_NEG;
		//Set initial distance between Mm and Ms
		Double initDistance = 2.0d * IUPACPeriodicTableOfElements.getInstance().getAtom(1).radius;
		logger.log("Processing NORMAL- complexes (khi=[-100; -80])");
		//Set normal complex flag to confine KAPPA angle to [0, 180]
		boolean isNormalComplex = true;
		while(mM.getPlaneAngle() <= ANGLE_NORMAL_MAX)
		{//KHI_CYCLE_START
			//Reset filter enabling flag at the end of khi cycle part (NORMAL+, PLANAR, NORMAL-)
			boolean isCycleEnded = false;
			logger.log(String.format("khi= %.2f", Math.toDegrees(mM.getPlaneAngle())));
			while(mM.getDiskAngle() < (isNormalComplex ? KAPPA_MAX_NORMAL : KAPPA_MAX))
			{//KAPPA_CYCLE_START
				while(mM.getRotationAngle() < DELTA_MAX)
				{//DELTA_CYCLE_START
					for(int i = 0; i < 2; ++i)
					{
						//Reset radius-vector
						mM.setRadiusVector(initDistance, 0.0d, 0.0d);
						//Set zenith angle 
						mM.setZenithAngle(THETA_PLANAR);
						//Check for intersection between Mm and Ms
						while(isMonomersIntersection())
						{
							//If there are intersection - move Mm to increase distance between Mm and Ms
							mM.incRx(dR);
						}
						//Perform checks in both mirrored and unmirrored mode
						//Check required H-bonds length 
						if(isNormalComplex) {
							//Additional cycle for checking interactions with all parts of Ms
							mM.setZenithAngle(THETA_MIN);
							while(mM.getZenithAngle() < THETA_MAX)
							{//THETA_CYCLE_START
								//Normal complexes
								for(int k = 0; k < bonds.size(); k = k + 2)
								{
									//Adjust H-bonds pair
									bond[0] = bonds.get(k);
									bond[1] = bonds.get(k + 1);
									if(isComplexCreated()) {//FIXME
										//Else check for distances between other donors and acceptors
										//If at least 1 distance is <Rmax - complex is invalid
										if(checkDonorAcceptorDistances()) {
											//Determine longest bond
											Double[] length = new Double[2];
											for(int m = 0; m < 2; ++m) {
												IAtom acceptor = bond[m].getAcceptor();
												acceptor = acceptor.getMolecule().getAtom(acceptor.getName());
												IAtom donor = bond[m].getDonor();
												donor = donor.getMolecule().getAtom(donor.getName());
												length[m] = acceptor.getPosition().sub(donor.getPosition()).length();
											}
											addFoundComplexes(getComplexName(), length[0] > length[1] ? length[0] : length[1]);
										}
									}
								}
								mM.incZenithAngle(dTheta);
							}//THETA_CYCLE_END
						} else {
							//Planar complexes
							for(int k = 0; k < bonds.size(); k = k + 2)
							{
								//Adjust H-bonds pair
								bond[0] = bonds.get(k);
								bond[1] = bonds.get(k + 1);
								if(isComplexCreated()) {//FIXME
									//Else check for distances between other donors and acceptors
									//If at least 1 distance is <Rmax - complex is invalid
									if(checkDonorAcceptorDistances()) {
										//Determine longest bond
										Double[] length = new Double[2];
										for(int m = 0; m < 2; ++m) {
											IAtom acceptor = bond[m].getAcceptor();
											acceptor = acceptor.getMolecule().getAtom(acceptor.getName());
											IAtom donor = bond[m].getDonor();
											donor = donor.getMolecule().getAtom(donor.getName());
											length[m] = acceptor.getPosition().sub(donor.getPosition()).length();
										}
										addFoundComplexes(getComplexName(), length[0] > length[1] ? length[0] : length[1]);
									}
								}
							}
						}
						mM.mirror();
					}
					mM.incRotationAngle(dDelta);
					//Continue while DELTA < 2*PI or (r>=Rmin && r<=Rmax)
				}//DELTA_CYCLE_END	
				//Rotate Mm along Ms on angle KAPPA
				mM.incDiskAngle(dKappa);
				//Reset DELTA
				mM.setRotationAngle(0.0d);
				//Continue while KAPPA < 2*PI
			}//KAPPA_CYCLE_END
			//Increase angle KHI
			mM.incPlaneAngle(dKhi);
			//Check for KHI being in range
			Double angKhi = mM.getPlaneAngle();
			if((angKhi > ANGLE_NORMAL_MMAX) && (angKhi < ANGLE_PLANAR_MIN)) {
				mM.setPlaneAngle(ANGLE_PLANAR_MIN);
				isNormalComplex = false;
				isCycleEnded = true;
			} else { 
				if((angKhi > ANGLE_PLANAR_MAX) && (angKhi < ANGLE_NORMAL_MIN)) {
					//If KHI not in range, then move to another range
					mM.setPlaneAngle(ANGLE_NORMAL_MIN);
					isNormalComplex = true;
					isCycleEnded = true;
				}
			}
			//Arm filter for the last angle
			if(angKhi > ANGLE_NORMAL_MAX) {
				isCycleEnded = true;
			}
			//Filter found complexes
			filterToBest(filterType, isCycleEnded);
			//Reset KAPPA
			mM.setDiskAngle(0.0d);
			//Filter and store filtered complexes after each part of the cycle
			if(isCycleEnded) {
				//Set filter type
				filterType = (EComplexType.NORMAL_NEG == filterType) ? EComplexType.PLANAR : EComplexType.NORMAL_POS;
				logger.log(String.format("Bond pairs found: %d", (null != complexes) ? complexes.size() : 0));
				if(null != complexes)
				{
					Iterator<String> complexName = complexes.keySet().iterator();
					while(complexName.hasNext())
					{
						String name = complexName.next();
						logger.log(String.format("Found complex class: %s", name));
					}
					//Store found results
					logger.log("Phase complete. Storing results...");
					storeResults();
					//Clear complexes for next part of Khi cycle
					complexes.clear();
				}
				//Log message about next part of Khi cycle
				if(isNormalComplex && (angKhi < ANGLE_NORMAL_MAX)) {
					logger.log("Processing NORMAL+ complexes (khi=[80; 100])");
				} else {
					if(angKhi < ANGLE_PLANAR_MAX) {
						logger.log("Processing PLANAR complexes (khi=[-10; 10])");
					}
				}
			}
			//If no other range available - terminate
		}//KHI_CYCLE_END
	}
	//Get constructed complexes
	@Override
	public List<IMolecule> getComplexes() throws DimerNoComplexesConstructedException {
		//If no complexes found - tell about it
		if(null == complexes)
			throw new DimerNoComplexesConstructedException();
		return complexes.values().iterator().next();
	}
	
	/*
	 *	Auxiliary methods
	 */
	//Define Ms and Mm molecules
	protected void defineMs(IMolecule m1, IMolecule m2) {
		if(m1.getAtomCount() >= m2.getAtomCount()) {
			this.mS = m1;
			this.mM = m2;
		} else {
			this.mS = m2;
			this.mM = m1;
		}
	}
	//Create complex name
	protected String getComplexName() {
		String[] strBond = new String[2];
		for(int i = 0; i < 2; ++i)
		{
			if(mS == bond[i].getAcceptor().getMolecule())
			{
				strBond[i] = bond[i].getAcceptor().getName() + bond[i].getDonor().getName();
			} else {
				strBond[i] = bond[i].getDonor().getName() + bond[i].getAcceptor().getName();
			}
			strBond[i] = strBond[i].toUpperCase();
		}
		return String.format("%s_%s-%s_%s", 
							 mS.getName(),
							 mM.getName(),
							 strBond[0],
							 strBond[1]
							);
	}
	//Create H-bonds name
	protected String getHBondsName() {
		String[] strBond = new String[2];
		for(int i = 0; i < 2; ++i)
		{
			if(mS == bond[i].getAcceptor().getMolecule())
			{
				strBond[i] = String.format("%s:%s", bond[i].getAcceptor().getName(), bond[i].getDonor().getName());
			} else {
				strBond[i] = String.format("%s:%s", bond[i].getDonor().getName(), bond[i].getAcceptor().getName());
			}
			strBond[i] = strBond[i].toUpperCase();
		}
		return String.format("%s %s",
							 strBond[0],
							 strBond[1]
							);
	}
	//Check if required H-bonds has been created
	protected boolean isComplexCreated() {
		boolean isCreated = true;
		for(int i = 0; i < 2; ++i)
		{
			//Get parameters of the bond
			Double rMin = bond[i].getRmin();
			Double rMax = bond[i].getRmax();
			//Get H-bond acceptor
			IAtom acceptor = bond[i].getAcceptor();
			acceptor = acceptor.getMolecule().getAtom(acceptor.getName());
			//Get H-bond donor
			IAtom donor = bond[i].getDonor();
			donor = donor.getMolecule().getAtom(donor.getName());
			//Calculate bond length
			Double distance = acceptor.getPosition().sub(donor.getPosition()).length();
			//Check if bond established
			isCreated = isCreated & (distance >= rMin) & (distance <= rMax);
			if(!isCreated) break;
		}
		return isCreated;
	}
	//Check atomic distances between atoms belonging to different monomers
	protected boolean isMonomersIntersection() {
		Iterator<IAtom> iAm = mM.getAtoms();
		while(iAm.hasNext())
		{
			IAtom aM = iAm.next();
			Vector3d posAm = aM.getPosition();
			Iterator<IAtom> iAs = mS.getAtoms();
			while(iAs.hasNext())
			{
				IAtom aS = iAs.next();
				Vector3d rAmAs = aS.getPosition().sub(posAm);
				Double rMin = aM.getVanDerWaalsRadius() + aS.getVanDerWaalsRadius();
				//If distance between Am and As less than Rmin = Rvw1 + Rvw2 - then complex is invalid
				if(rAmAs.length() < rMin)
					return true;
			}
		}
		return false;
	}
	//Check atomic distances between H-bond donors/acceptors and non-bond acceptors/donors accordingly
	protected boolean checkDonorAcceptorDistances() {
		for(int i = 0; i < 2 ; ++i)
		{
			//Define Rmax
			Double rMax = bond[i].getRmax();
			//Get H-bond donor
			IAtom bondDonor = bond[i].getDonor();
			bondDonor = bondDonor.getMolecule().getAtom(bondDonor.getName());
			//Get H-bond acceptor
			IAtom bondAcceptor = bond[i].getAcceptor();
			bondAcceptor = bondAcceptor.getMolecule().getAtom(bondAcceptor.getName());
			//Get acceptors
			Iterator<IAtom> iAcceptors = (mM == bondDonor.getMolecule()) ? mS.getAcceptors().iterator() : mM.getAcceptors().iterator();
			//Get donors
			Iterator<IAtom> iDonors = (mM == bondAcceptor.getMolecule()) ? mS.getDonors().iterator() : mM.getDonors().iterator();
			//Iterate through all acceptors to check distances between them and H-bond donor
			while(iAcceptors.hasNext())
			{
				IAtom acceptor = iAcceptors.next();
				//Skip H-bond acceptor check. Validity of this kind of distance must be performed in isComplexCreated() 
				if(bondAcceptor.getName() == acceptor.getName())
					continue;
				//Calculate distance between H-bond donor and current acceptor
				Double distance = bondDonor.getPosition().sub(acceptor.getPosition()).length();
				//If it's less than Rmax, then complex invalid
				if(distance < rMax)
					return false;
			}
			//Iterate through all donors to check distances between them and H-bond acceptor
			while(iDonors.hasNext())
			{
				IAtom donor = iDonors.next();
				//Skip H-bond donor check. Validity of this kind of distance must be performed in isComplexCreated()
				if(bondDonor.getName() == donor.getName())
					continue;
				//Calculate distance between H-bond acceptor and current donor
				Double distance = bondAcceptor.getPosition().sub(donor.getPosition()).length();
				//If it's less than Rmax, then complex invalid
				if(distance < rMax)
					return false;
			}
		}
		return true;
	}
	//Add found complexes to storage
	protected void addFoundComplexes(String complexName, Double maxBondLength) {
		//Create container for found complexes
		if(null == complexes)
			complexes = new TreeMap<String, List<IMolecule>>();
		//Create complex from Ms and Mm
		IMolecule complex = new DimerComplex(complexName, maxBondLength);
		//Add Ms atoms
		Iterator<IAtom> atoms = mS.getAtoms();
		while(atoms.hasNext())
		{
			IAtom atom = atoms.next();
			complex.addAtom(atom);
		}
		//Add Mm atoms
		atoms = mM.getAtoms();
		while(atoms.hasNext())
		{
			IAtom atom = atoms.next();
			complex.addAtom(atom);
		}
		//Store angles and radius-vector module
		complex.setPlaneAngle(mM.getPlaneAngle());
		complex.setRotationAngle(mM.getRotationAngle());
		complex.setDiskAngle(mM.getDiskAngle());
		complex.setRadiusVector(mM.getRadiusVector());
		//Storage complex
		if(!complexes.containsKey(complexName)) {
			//First time
			List<IMolecule> complexesList = new LinkedList<IMolecule>();
			complexesList.add(complex);
			complexes.put(complexName, complexesList);
		} else {
			complexes.get(complexName).add(complex);
		}
	}
	//Filter found complexes for the best
	protected void filterToBest(EComplexType type, boolean isPrefixAllowed) {
		//If no complexes found - do nothing
		if((null == complexes) || complexes.isEmpty()) {
			return;
		}
		logger.log("Complex construction complete. Filtering...", true);
		//Set MIN, MID, MAX angles to check
		final double ANGLE_MIN = EComplexType.PLANAR == type ? ANGLE_PLANAR_MIN : 
						   (EComplexType.NORMAL_NEG == type ? ANGLE_NORMAL_MMIN : ANGLE_NORMAL_MIN);
		final double ANGLE_MID = EComplexType.PLANAR == type ? ANGLE_PLANAR_MID : 
						   (EComplexType.NORMAL_NEG == type ? ANGLE_NORMAL_MMID : ANGLE_NORMAL_MID);
		final double ANGLE_MAX = EComplexType.PLANAR == type ? ANGLE_PLANAR_MAX : 
						   (EComplexType.NORMAL_NEG == type ? ANGLE_NORMAL_MMAX : ANGLE_NORMAL_MAX);
		final String prefixMin = EComplexType.PLANAR == type ? "-p1" : 
						   (EComplexType.NORMAL_NEG == type ? "-nm1" : "-n1");
		final String prefixMid = EComplexType.PLANAR == type ? "-p0" : 
						   (EComplexType.NORMAL_NEG == type ? "-nm0" : "-n0");
		final String prefixMax = EComplexType.PLANAR == type ? "-p2" : 
			   			   (EComplexType.NORMAL_NEG == type ? "-nm2" : "-n2");
		//Found complexes
		Iterator<String> names = complexes.keySet().iterator();
		while(names.hasNext())
		{
			IMolecule bestMin = null;
			IMolecule bestMid = null;
			IMolecule bestMax = null;
			String name = names.next();
			List<IMolecule> foundComplexes = complexes.get(name);
			Iterator<IMolecule> molecules = foundComplexes.iterator();
			//Filter cycle
			while(molecules.hasNext())
			{
				IMolecule complex = molecules.next();
				Double angle = complex.getPlaneAngle();
				Double complexMaxLength = ((IDimerComplex) complex).getMaxBondLength();
				//MIN angle
				if(ANGLE_MIN == angle) {
					if(null == bestMin) {
						bestMin = complex;
						continue;
					} else {
						if(complexMaxLength < ((IDimerComplex) bestMin).getMaxBondLength())
							bestMin = complex;
					}
				}
				//MID angle
				if(ANGLE_MID == angle) {
					if(null == bestMid) {
						bestMid = complex;
						continue;
					} else {
						if(complexMaxLength < ((IDimerComplex) bestMid).getMaxBondLength())
							bestMid = complex;
					}
				}
				//MAX angle
				if(ANGLE_MAX == angle) {
					if(null == bestMax) {
						bestMax = complex;
						continue;
					} else {
						if(complexMaxLength < ((IDimerComplex) bestMax).getMaxBondLength())
							bestMax = complex;
					}
				}
			}
			//Remember found complexes
			String bestMidName = null;
			String bestMinName = null;
			String bestMaxName = null;
			if(null != bestMid) {
				bestMidName = isPrefixAllowed ? bestMid.getName().concat(prefixMid) : bestMid.getName();
				bestMid = new DimerComplex(bestMidName , (DimerComplex) bestMid);
			} else {
				if(null != bestMin) {
					bestMinName = isPrefixAllowed ? bestMin.getName().concat(prefixMin) : bestMin.getName();
					bestMin = new DimerComplex(bestMinName, (DimerComplex) bestMin);
				}
				if(null != bestMax) {
					bestMaxName = isPrefixAllowed ? bestMax.getName().concat(prefixMax) : bestMax.getName();
					bestMax = new DimerComplex(bestMaxName, (DimerComplex) bestMax);
				}
			}
			//Clear complexes
			foundComplexes.clear();
			//Repopulate complexes
			if(null != bestMid) {
				foundComplexes.add(bestMid);
			} else {
				if(null != bestMin) {
					foundComplexes.add(bestMin);
				}
				if(null != bestMax) {
					foundComplexes.add(bestMax);
				}
			}
		}
		logger.log("done");
	}
	//Store results
	protected void storeResults() {
		//If no complexes found - do nothing
		if((null == complexes) || complexes.isEmpty()) {
			logger.log(String.format("Nothing to store! (TID: %d)", super.getId()));
			return;
		}
		//Otherwise - store results
		Iterator<List<IMolecule>> foundComplexes = complexes.values().iterator();
		while(foundComplexes.hasNext())
		{
			Iterator<IMolecule> dimers = foundComplexes.next().iterator();
			while(dimers.hasNext())
			{
				IMolecule dimer = dimers.next();
				storage.store(dimer);
				logger.log(String.format("Stored %s", dimer.getName()));
			}
		}
	}
	
	
	/*
	 *	Constructor
	 */
	//Initialization constructor
	public Dimer(IJobDescriptor job) {
		//Define stationary and movable monomers
		defineMs(job.getMonomer1(), job.getMonomer2());
		//Get H-bonds
		bonds = job.getHBonds();
		//Get job name
		jobName = job.getJobName();
	}
}
