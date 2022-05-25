# Introduction-to-Cryptography-Laboratory
 The Homework from the Introduction to Cryptography Laboratory. <br /> <br />

# Homework 1
Implementation of Vigenere and Substitution Cryptosystems encipher and decipher (with the stantard English language frequencies for letters), using letter, bigram and trigram frequency analysis and index of coincidence & mutual index of coincidence (for the Vigenere Cryptosystem, for finding the key length & key). <br /> <br />

# Homework 2
Implementation of two pseudo random number generators, Blum-Blum-Shub Generator (using parity of seed^2 modulo N as bit generation) and Jacobi Generator (using the Jacobi Symbol of the seeds and N as bit generation). (N (represented on 1024 bits) is a product of two big primes, represented on 512 bits each) <br />
We also conducted two tests for each generator, a frequency test on the amount of 0s and 1s in the bitstring and a compression test, comparing the compression of generated bitstring with the compression of a repeating pattern (like '010101...'). <br /> <br />

# Homework 3
Implementation on a LFSR-based pseudo random number generator. We start with an initial configuration of REGISTER_SIZE bits (this configuration must be different than all zeros), we add the least significant bit to the outputbitString (number), we shift the bitString to the right we calculate it's most significant bit by using the connection polynomial (xor-ing those coefficients). We repeat that MAX_ITERATIONS times. <br />
__Time taken:__ For the purposes of this and the previous generators tests, we set MAX_ITERATIONS to 1 milion (__10^6__). We can see that this generator is way faster than the previous ones (__LFSR single-threaded__: average 0.14 seconds) (__BBS single-threaded__: average 10 seconds, __BBS multi-threaded__: average: 4.5 seconds) (__Jacobi single-threaded__: average 11.5 seconds, __Jacobi multi-threaded__: 5.5 seconds). (number of threads for the multi-thread runs: 10) <br />
We also run a generator period test. If the connection polynomial is set right, the generator period should be __2^REGISTER_SIZE - 1__. <br />
Implementation of the RC4 stream cryptosystem. We generate a keyStream of needed length using the keySchedulingAlgorithm and pseudoRandomGenerationAlgorithm when we start the generator. <br />
We encrypt and decrypt by using XOR: __encryptedText = plainText XOR keyStream__ and __plainText = encryptedText XOR keyStream__. <br />
We run a test to check if the algorithm has the following bias: the second byte generated in the keyStream has a 1/128 probability to be 0 (instead of 1/256 as would be normal), for different random keys. We iterate 10000 times, generating random keys and keyStreams of length >=2, and afterwards we check the probability of that byte to be 0.  <br /> <br />


# Homework 4
Implementation of the DES Cryptosystem with both encryption and decryption with the following steps: <br />
* We give it a key, it generates the 16 keys needed for the encryption / decryption <br />
* We read the plainText / encryptedText from an input file <br />
* For encryption: <br />
    - We split the plain text into equal blocks of 64 bits (8 bytes). If there is a remaining block that is not 64 bits long, we fill it with space characters. <br />
    - We take each block, apply the initial permutation and split it <br />
    - for each round, we use the formula: <br />
        1. Left(n) = Right(n - 1); <br />
        2. Right(n) = Left(n - 1) XOR FeistelFunction(Right(n - 1), Keys(n - 1)); <br />
    - we use the keys in order for the rounds (from 1 to 16) <br />
    - we apply the final permutation <br />
    - we add each encrypted block to the final encrypted text <br />
* For decryption: <br />
    - We split the encrypted text into equal blocks of 64 bits (8 bytes) . If there is a remaining block that is not 64 bits long, we fill it with space characters. <br />
    - We take each block, apply the initial permutation and split it <br />
    - for each round, we use the formula: <br />
        1. Left(n) = Right(n - 1); <br />
        2. Right(n) = Left(n - 1) XOR FeistelFunction(Right(n - 1), Keys(16 - (n - 1))); <br />
    - we use the keys in reverse order for the rounds (from 16 to 1) <br />
    - we apply the final permutation <br />
    - we add each decrypted block to the final plain text <br />

For the purposes of the following two tests the keys will be of form "b^8", where "b" is a byte. <br />

Implementation of the Double-DES (2DES) Cryptosystem with both encryption and decryption. We use two different 8-byte size keys, applying DES encryption with the first key, then with the second key on the output after the first key for encryption, and applying DES decryption with the second key, then with the first key on the output after the second key for decryption. <br />

Implementation of the Man-in-the-Middle-Attack for 2DES Cryptosystem. We know a pair of (plainText, encryptedText) from a 2DES encryption.  <br />
We encrypt the plainText with des with all 256 possible keys for the first key (of form "b^8), saving the output and the key at each step. <br /> 
We then decrypt the encryptedText with all the 256 possible keys for the second key, and when we find the text we decrypted appears in the outputs saved from the previous test, we memorize the pair (first key, second key) as a possible key pair used for the 2DES cryptosystem. <br /> <br />

# Homework 5
Implementation of SHA-1. SHA-1 takes an input plainText and returns a string (which is actually a 64-bit hexadecimal number) of length 40 (in hexadecimal characters, i.e. 160 bits in length). <br />
After doing the preprocessing, i.e. converting the message to a bit string, appending "0"'s to it until it has a length congruent to 448 modulo 512, and then appending the original message length as a 64-bit integer's bitstring, we do the processing. <br />
The processing has the following stept: <br />
* we breal the bitString message into 512-bit chunks which we process <br />
* we break each chunk into 16 32-bit words <br />
* we extens those 16 words into 80 words <br />
* we initialize the hash values of the current chuck (using the constants H0, H1, H2, H3 and H4) <br />
* in the main loop, depending on the current iteration, we chose a function which does calculations with our "b", "c" and "d" values, and we choose one of the K0, K1, K2, K3 constants. After this, we calculate the sum of some of the values into a temporary value, and swap the main numbers with each other. <br />
* finally, we add the chunk's hash to the result (by summing the "H" constants with the main values after the loop) <br />
* we append those values together and we get the hexadecimal string hash. <br /> <br />
We implement 4 tests: <br />
* the first one tests if the length of the obtained hash on a string is 40 characters (hexadecimal), i.e. 160 bits long. <br />
* the second and third tests check that the intermediate steps as shown in two test vectors are followed in our algorithm for hashing the given plainTexts <br />
* the fourth test checks the cascade effect (by making a very small change to the input text, the hashes will be significantlly different), by calculating the Hamming distance and seeing if it's big enough to pass. <br /> <br />

Implementation of a birthday attack on a simplified 32-bit hash function.
We first implement the class FakeSHA1Hasher, which does the SHA1 hashing, but only keeps the first 32 bits (8 hexadecimal characters) of the hash. <br />
We then implement the BirthdayAttack, which will first generate 2^(32/2) small changes to the original text, such that their hashes are different. This process, although implemented with multithreading, is slow, so we will serialize the saved data in order to load it for other runs. <br />
We store the hashes along with the minor-changed texts into a HashMap (it maps a hash to it's text). Therefore, the time complexity for a search for a hash in this data structure will be O(1). <br />
Afterwards, we try generate up to 2^(32/2) small changes to the attacker's text, such that their hashes are different, checking at each step if the hash we got appears in the map build for the original text's saved hashes (for the minor changes). <br /> <br />

# Homework 6
Implementation of the RSA Cryptosystem. First, we setup our constants (for the public and private key): we generate two prime numbers, __P__ and __Q__, on 1024 bits each, and we get their product, __N__. We calculate __phiN = (P - 1) * (Q - 1)__. We generate __E__ on 32-bits, coprime with phiN. We generate __D__, such that __D * E = -1 mod phiN__. <br />
The encryption is done by doing __plainText^E mod N__, and de decryption is done with __encryptedText^D mod N__. Since D is a very big number we implement a way to raise to the power D by dynamic programming: calculate the powers of two up until D, then get D's binary representation and calculate the result. <br />
We also implement a test, which tests that __decrypt(encrypt(text))  = text__. <br /> <br />

For the second part, we try to make the decryption even faster, by doing it using the Chinese Remainder Theorem (solving the system gives us the solution to the power-raise calculation). <br /> <br />

For the last part, we implement __Wiener's attack__. We first need a different setup for the RSACryptosystem: we make sure that when we generate P and Q, __P < Q < 2 * P__ (or __Q < P < 2 * Q__). Then we generate D on 512 bits, and afterwards we find E (s.t. D * E = -1 mod phiN). <br />
The attack goes as follows: we need to find the private key (D, P, Q); we generate the convergent functions of __E / N__ until we find a solution (which is a whole number). When we find a solution, we also found a candidate for __D__ and __phiN__. In order to find if the solution is good, we check that the equation __x^2 - (N - phiN + 1) * x + N = 0__ has integer solutions. If true, we've found our solution __(D, phiN)__, else we keep going until we can't anymore. After knowing __N__ and __phiN__, finding __P__ and __Q__ is trivial. <br /> <br />