/** The multivariate Normal distribution $N(\mu, \Sigma)$.
An instance of a NormalDistribution class generates realizations of a random
variable $X$ with probability density function 
$$f_X(x) = |\Sigma|^{-1/2}(2\pi)^{-n/2}\exp\left(-\frac{1}{2}(x-\mu)'\Sigma^{-1}(x-\mu)\right).$$

@author Y. Zwols
@sortkey C

@example To generate 20 samples from a standard normal distribution, the following code
can be used:
<pre>
decl Dist = new NormalDistribution(0, 1);
decl Z = Dist.Generate(20);
</pre>
**/
class NormalDistribution : Distribution {
	decl m_vMu, m_mSigma;
	NormalDistribution(const vMu, const mSigma);
	virtual Generate(const cT);
	virtual Dim();
}

/** Create a new instance of the NormalDistribution class with parameters $\mu$ and $\Sigma$.
@param vMu The mean $\mu$ of the normal distribution
@param mSigma The variance/covariance matrix
@comments The dimension of the multivariate normal distribution is deduced
from the dimensions of the arguments. **/
NormalDistribution::NormalDistribution(const vMu, const mSigma) {
	expectMatrix("vMu", vMu, rows(vMu), 1);
	expectMatrix("mSigma", mSigma, rows(vMu), rows(vMu));
	m_vMu = vMu;
	m_mSigma = mSigma;
}

/** Generate a vector of realizations. The length of the sample is given
by the argument cT.
@param cT Number of samples 
**/
NormalDistribution::Generate(const cT) {
	return rann(cT, Dim());
}

/** The dimension of the multivariate normal distribution.
@comments This is deduced from the arguments given to the constructor. **/
NormalDistribution::Dim() {
	return rows(m_vMu);
}

