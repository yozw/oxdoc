/** The multivariate Normal distribution $\mathcal{N}(\mu, \Sigma)$.
@author Y. Zwols

@example To generate 20 samples from a standard normal distribution, the following code
can be used:
<pre>
decl Dist = new NormalDistribution(0, 1);
decl Z = Dist.Generate(20);
**/
class NormalDistribution : Distribution {
	decl m_vMu, m_mSigma;
	NormalDistribution(const vMu, const mSigma);
	virtual Generate(const cT);
	virtual Dim();
}

/** Create a new instance of the NormalDistribution class with parameters $\mu$ and $\Sigma$ **/
NormalDistribution::NormalDistribution(const vMu, const mSigma) {
	expectMatrix("vMu", vMu, rows(vMu), 1);
	expectMatrix("mSigma", mSigma, rows(vMu), rows(vMu));
	m_vMu = vMu;
	m_mSigma = mSigma;
}

NormalDistribution::Generate(const cT) {
	return rann(cT, Dim());
}

NormalDistribution::Dim() {
	return rows(m_vMu);
}

