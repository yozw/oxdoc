/** Provides the `Distribution` class, representing an abstract random
distribution
@author Y. Zwols
@sortkey A
**/

#ifndef __PCLIRE_DISTRIBUTION__
#define __PCLIRE_DISTRIBUTION__

enum { HEADER, REGULAR };


/** Testing global variables. **/ static decl v;

/** Testing global constants. **/ const decl z = 9;




/** Abstract class that represents a random distribution. Classes derived from the base class
Distribution offer functionality like generating samples. Also, a Distribution has a dimension.

@see NormalDistribution, DegenerateDistribution
**/
class Distribution {
	virtual Generate(const cT);
	virtual Dim();
	static isDistribution(oObject);
}

/** Generates a random sample from the distribution **/
Distribution::Generate(const cT) {
	error("Distribution::Generate() not implemented; use a derived class.");
}

/** Generates a random sample from the distribution **/
Distribution::Dim() {
	error("Distribution::Dim() not implemented; use a derived class.");
}

/** @sortkey aaa @internal Checks whether a certain object is a distribution.
@returns TRUE or FALSE 

**/
Distribution::isDistribution(oObject) {
	 return isclass(oObject, "Distribution");
}

#endif

