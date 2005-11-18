/** Provides standard distributions **/

/** The degenerate distribution.
@author Y. Zwols
**/
class DegenerateDistribution : Distribution {
	decl m_vC;
	DegenerateDistribution(const vC);
	virtual Generate(const cT);
	virtual Dim();
}

DegenerateDistribution::DegenerateDistribution(const vC) {
	expectMatrix("vC", vC, rows(vC), 1);
	m_vC = vC;
}

DegenerateDistribution::Generate(const cT) {
	return ones(cT, 1) ** m_vC';
}

DegenerateDistribution::Dim() {
	return rows(m_vC);
}

