package com.achawki.sequencetrainer.math

/**
 * Determines and returns next prime number.
 * Not most efficient implementation, but not very large numbers are expected.
 *
 * @param n the base for the next prime number
 * @return next prime
 */
fun nextPrime(n: Int): Int {
    if (n < 2) return 2

    var prime = n + 1

    while (!isPrime(prime)) {
        prime++
    }
    return prime
}

private fun isPrime(n: Int): Boolean {
    if (n < 2) return false

    var i = 2
    while (i * i <= n) {
        if (n % i == 0) return false
        i++
    }

    return true
}
