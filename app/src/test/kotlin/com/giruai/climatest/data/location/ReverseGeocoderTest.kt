package com.giruai.climatest.data.location

import android.content.Context
import android.location.Address
import android.location.Geocoder
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.IOException
import java.util.Locale

@RunWith(RobolectricTestRunner::class)
class ReverseGeocoderTest {

    private lateinit var context: Context
    private lateinit var reverseGeocoder: ReverseGeocoder

    @Before
    fun setup() {
        context = mockk(relaxed = true)
        reverseGeocoder = ReverseGeocoder(context)
    }

    @Test
    fun `formatCoordinates returns correct format`() = runTest {
        // When geocoder is not available, falls back to coordinates
        mockkConstructor(Geocoder::class)
        every { anyConstructed<Geocoder>().getFromLocation(any(), any(), any()) } returns emptyList()

        val result = reverseGeocoder.getCityName(-34.6037, -58.3816)
        assertEquals("-34.60°, -58.38°", result)
    }

    @Test
    fun `getCityName returns city and country when available`() = runTest {
        val address = mockk<Address>()
        every { address.locality } returns "Buenos Aires"
        every { address.countryCode } returns "AR"
        every { address.subAdminArea } returns null
        every { address.adminArea } returns null
        every { address.countryName } returns "Argentina"

        mockkConstructor(Geocoder::class)
        every { anyConstructed<Geocoder>().getFromLocation(any(), any(), any()) } returns listOf(address)

        val result = reverseGeocoder.getCityName(-34.6037, -58.3816)
        assertEquals("Buenos Aires, AR", result)
    }

    @Test
    fun `getCityName falls back to coords on IOException`() = runTest {
        mockkConstructor(Geocoder::class)
        every { anyConstructed<Geocoder>().getFromLocation(any(), any(), any()) } throws IOException("Network error")

        val result = reverseGeocoder.getCityName(48.8566, 2.3522)
        assertEquals("48.86°, 2.35°", result)
    }

    @Test
    fun `getCityName uses cache on second call`() = runTest {
        val address = mockk<Address>()
        every { address.locality } returns "Paris"
        every { address.countryCode } returns "FR"
        every { address.subAdminArea } returns null
        every { address.adminArea } returns null
        every { address.countryName } returns "France"

        mockkConstructor(Geocoder::class)
        every { anyConstructed<Geocoder>().getFromLocation(any(), any(), any()) } returns listOf(address)

        // First call
        val result1 = reverseGeocoder.getCityName(48.8566, 2.3522)
        assertEquals("Paris, FR", result1)

        // Second call should use cache (even if geocoder throws)
        every { anyConstructed<Geocoder>().getFromLocation(any(), any(), any()) } throws IOException("Should not be called")
        val result2 = reverseGeocoder.getCityName(48.8566, 2.3522)
        assertEquals("Paris, FR", result2)
    }

    @Test
    fun `getCityName falls back to adminArea when locality is null`() = runTest {
        val address = mockk<Address>()
        every { address.locality } returns null
        every { address.countryCode } returns "US"
        every { address.subAdminArea } returns null
        every { address.adminArea } returns "California"
        every { address.countryName } returns "United States"

        mockkConstructor(Geocoder::class)
        every { anyConstructed<Geocoder>().getFromLocation(any(), any(), any()) } returns listOf(address)

        val result = reverseGeocoder.getCityName(37.7749, -122.4194)
        assertEquals("California, US", result)
    }

    @Test
    fun `getCityName skips postal code locality and uses subAdminArea`() = runTest {
        val address = mockk<Address>()
        every { address.locality } returns "C1070AAJ"  // Argentine postal code
        every { address.countryCode } returns "AR"
        every { address.subAdminArea } returns "Buenos Aires"
        every { address.adminArea } returns "Ciudad Autónoma de Buenos Aires"
        every { address.countryName } returns "Argentina"

        mockkConstructor(Geocoder::class)
        every { anyConstructed<Geocoder>().getFromLocation(any(), any(), any()) } returns listOf(address)

        val result = reverseGeocoder.getCityName(-34.6037, -58.3816)
        assertEquals("Buenos Aires, AR", result)
    }

    @Test
    fun `getCityName skips generic district and uses adminArea`() = runTest {
        val address = mockk<Address>()
        every { address.locality } returns "C1070AAJ"  // Argentine postal code
        every { address.countryCode } returns "AR"
        every { address.subAdminArea } returns "Comuna 1"  // Generic district
        every { address.adminArea } returns "Ciudad Autónoma de Buenos Aires"
        every { address.countryName } returns "Argentina"

        mockkConstructor(Geocoder::class)
        every { anyConstructed<Geocoder>().getFromLocation(any(), any(), any()) } returns listOf(address)

        val result = reverseGeocoder.getCityName(-34.6037, -58.3816)
        assertEquals("Ciudad Autónoma de Buenos Aires, AR", result)
    }
}
