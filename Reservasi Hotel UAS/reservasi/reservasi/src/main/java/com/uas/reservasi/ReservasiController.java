package com.uas.reservasi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@Controller
public class ReservasiController {

    @Autowired
    private ReservasiRepository repository;
    
    // Anggap saja hotel kita punya total 5 kamar
    private final long TOTAL_KAMAR = 5; 

    @GetMapping("/")
    public String halamanUtama(Model model) {
        // Hitung sisa kamar khusus untuk HARI INI untuk ditampilkan di halaman atas
        LocalDate hariIni = LocalDate.now();
        long terpakaiHariIni = repository.hitungKamarTerpakai(hariIni, hariIni);
        long sisaKamar = TOTAL_KAMAR - terpakaiHariIni;

        model.addAttribute("sisaKamar", sisaKamar);
        model.addAttribute("daftarReservasi", repository.findAll());
        model.addAttribute("reservasiBaru", new Reservasi());
        return "index"; 
    }

    @PostMapping("/simpan")
    public String simpanReservasi(@ModelAttribute("reservasiBaru") Reservasi reservasi, Model model) {
        try {
            // 1. Validasi Tanggal Terbalik
            if (reservasi.getTanggalCheckIn().isAfter(reservasi.getTanggalCheckOut())) {
                throw new Exception("Gagal: Tanggal Check-In tidak boleh melewati Tanggal Check-Out!");
            }

            // 2. Validasi Kamar Penuh di Tanggal Tersebut
            long terpakai = repository.hitungKamarTerpakai(reservasi.getTanggalCheckIn(), reservasi.getTanggalCheckOut());
            if (terpakai >= TOTAL_KAMAR) {
                throw new Exception("Gagal: Kamar penuh untuk tanggal tersebut! Silakan pilih rentang tanggal lain.");
            }

            // Jika aman, simpan
            repository.save(reservasi);
            return "redirect:/";

        } catch (Exception e) {
            // Tampilkan error, tapi pastikan info sisa kamar hari ini tetap dimunculkan
            LocalDate hariIni = LocalDate.now();
            long sisaKamar = TOTAL_KAMAR - repository.hitungKamarTerpakai(hariIni, hariIni);
            
            model.addAttribute("sisaKamar", sisaKamar);
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("daftarReservasi", repository.findAll());
            return "index"; 
        }
    }
    
    // 3. Menghapus data reservasi
    @GetMapping("/hapus/{id}")
    public String hapusReservasi(@PathVariable Long id) {
        repository.deleteById(id);
        return "redirect:/";
    }

    // 4. Menyiapkan data untuk diedit
    @GetMapping("/edit/{id}")
    public String editReservasi(@PathVariable Long id, Model model) {
        Reservasi reservasi = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("ID tidak ditemukan"));
        model.addAttribute("daftarReservasi", repository.findAll());
        model.addAttribute("reservasiBaru", reservasi); // Memasukkan data lama ke form
        return "index";
    }
}