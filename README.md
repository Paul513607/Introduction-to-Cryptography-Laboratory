# Introduction-to-Cryptography-Laboratory
 The Homework from the Introduction to Cryptography Laboratory. <br />

# Homework 1
Implementation of Vigenere and Substitution Cryptosystems encipher and decipher (with the stantard English language frequencies for letters), using letter, bigram and trigram frequency analysis and index of coincidence & mutual index of coincidence (for the Vigenere Cryptosystem, for finding the key length & key). <br />

# Homework 2
Implementation of two pseudo random number generators, Blum-Blum-Shub Generator (using parity of seed^2 modulo N as bit generation) and Jacobi Generator (using the Jacobi Symbol of the seeds and N as bit generation). (N (represented on 1024 bits) is a product of two big primes, represented on 512 bits each) <br />
We also conducted two tests for each generator, a frequency test on the amount of 0s and 1s in the bitstring and a compression test, comparing the compression of generated bitstring with the compression of a repeating pattern (like '010101...'). <br />

# Homework 3
Implementation on a LFSR-based pseudo random number generator. We start with an initial configuration of REGISTER_SIZE bits (this configuration must be different than all zeros), we add the least significant bit to the outputbitString (number), we shift the bitString to the right we calculate it's most significant bit by using the connection polynomial (xor-ing those coefficients). We repeat that MAX_ITERATIONS times. <br />
__Time taken:__ For the purposes of this and the previous generators tests, we set MAX_ITERATIONS to 1 milion (__10^6__). We can see that this generator is way faster than the previous ones (__LFSR single-threaded__: average 0.14 seconds) (__BBS single-threaded__: average 10 seconds, __BBS multi-threaded__: average: 4.5 seconds) (__Jacobi single-threaded__: average 11.5 seconds, __Jacobi multi-threaded__: 5.5 seconds). (number of threads for the multi-thread runs: 10) <br />
We also run a generator period test. If the connection polynomial is set right, the generator period should be __2^REGISTER_SIZE - 1__. <br />
Implementation of the RC4 stream cryptosystem. We generate a keyStream of needed length using the keySchedulingAlgorithm and pseudoRandomGenerationAlgorithm when we start the generator. <br />
We encrypt and decrypt by using XOR: __encryptedText = plainText XOR keyStream__ and __plainText = encryptedText XOR keyStream__. <br />
We run a test to check if the algorithm has the following bias: the second byte generated in the keyStream has a 1/128 probability to be 0 (instead of 1/256 as would be normal), for different random keys. We iterate 10000 times, generating random keys and keyStreams of length >=2, and afterwards we check the probability of that byte to be 0.  <br />


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
We then decrypt the encryptedText with all the 256 possible keys for the second key, and when we find the text we decrypted appears in the outputs saved from the previous test, we memorize the pair (first key, second key) as a possible key pair used for the 2DES cryptosystem. <br />

# Homework 5

# Homework 6
