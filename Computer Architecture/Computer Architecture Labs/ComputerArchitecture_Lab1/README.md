**Ναπολέων Παπουτσάκης 9170**

**Γιώργος Εφραίμ Παππάς 9124**

# Αναφορά Εργαστηρίου 1

  Σκοπός του πρώτου εργαστηρίου του μαθήματος είναι η εξοικείωση με τον εξομοιωτή _**gem5**_. Έτσι, αφού καταλάβουμε το ποια είνα τα βασικά χαρακτηριστικά του συστήματος και τα αναγνωρίσουμε στα κατάλληλα αρχεία καλούμαστε να κατασκευάσουμε ενα δικό μας πρόγραμμα σε γλώσσα _**C**_, όπου θα παρατηρήσουμε τι αλλαγές συμβαίνουν για τα διαφορετικά μοντέλα _CPU_ και θα αλλάξουμε κάποιες από τις παραμέτρους.

**Ερώτημα 1**

Αρχικά, εκτελώντας την εντολή:
 `$ ./build/ARM/gem5.opt configs/example/arm/starter_se.py --cpu="minor"
"tests/test-progs/hello/bin/arm/linux/hello"`
για να τρέξει το _precompiled_ πρόγραμμα, ορίζουμε το μοντέλο του επεξεργαστή σε _**minor**_. Οι υπόλοιπες παράμετροι του _gem5_ ορίζονται αυτόματα στο _**starter_se.py**_.

Ανοίγοντας το _starter_se.py_ βλέπουμε ότι οι υπόλοιπες παράμετροι έχουν αρχικοποιηθεί στη _main()_ ως εξής:

  * Η **συχνότητα** του επεξεργαστή ορίστηκε σε _**4Ghz**_ όπως φαίνεται εδώ:
  `parser.add_argument("--cpu-freq", type=str, default="4GHz")`
  
  * Το **πλήθος** επεξεργαστών σε **1** σύμφωνα με `parser.add_argument("--num-cores", type=int, default=1, help="Number of CPU cores")`
  
  * **Τύπος** μνήμης _**DDR3@1600Mhz_8x8**_ `parser.add_argument("--mem-type", default="DDR3_1600_8x8",choices=ObjectList.mem_list.get_names(),help = "type of memory to use")`
  
  * **Κανάλια** μνήμης σε **2** (_**dual channel**_)  `parser.add_argument("--mem-channels", type=int, default=2,help = "number of memory channels")`
  
  * Το **μέγεθος** της μνήμης σε **_2GB_** `parser.add_argument("--mem-size", action="store", type=str,default="2GB", help="Specify the physical memory size")`
  
  * Η **τάση** λειτουργείας του **συστήματος** και το _**clock**_ ως _**3.3V**_ και **_1Ghz_** αντίστοιχα καθώς και η τάση λειτουργίας του επεξεργαστή ως _**1.2V**_ οπως φαινεται στις παρακάτω παραθέσεις τμημάτων του κώδικα του αρχείου αυτου: 
    * `root.system = create(args)` ( της _main()_ ) 
    * `system = SimpleSeSystem(args)` ( της _create(args)_ )
    * `self.voltage_domain = VoltageDomain(voltage="3.3V")   
      self.clk_domain = SrcClockDomain(clock="1GHz",
                                         voltage_domain=self.voltage_domain)
     self.cpu_cluster = devices.CpuCluster(self,
                                              args.num_cores,
                                              args.cpu_freq, "1.2V",
                                              *cpu_types[args.cpu])`    (της class _SimpleSeSystem(System)_)                        
   * Στον _gem5_ δημιουργούνται επίσης 2 **επίπεδα** μνήμης **_cache_** (**_L1,L2_**) 
     
     ( αρχείο _**starter_se.py**_ -> _class SimpleSeSystem(System)_ ->  `if self.cpu_cluster.memoryMode() == "timing":
            self.cpu_cluster.addL1()
            self.cpu_cluster.addL2(self.cpu_cluster.clk_domain)` )
            
     Για την _**L1I**_: έχουμε 
      _**size**_ = '48kB' και
      _**assoc**_ = 3 (Συσχέτιση) 
      _**response latency**_ = 1ns
      
      _**Cache Line Size**_ = 64bytes
     
     Για την _**L1D**_: έχουμε 
      _**size**_ = '32kB',
      _**assoc**_ = 2 (Συσχέτιση)      _**response latency**_ = 1ns
      _**write_buffers**_  = 16
  
      _**Cache Line Size**_ = 64bytes 

     Για την _**L2**_: έχουμε
     **size**_ = '1ΜB' 
      _**assoc**_ = 16(Συσχέτιση) 
      _**response latency**_ = 5ns
     _**write_buffers**_  = 8

     _**Cache Line Size**_ = 64bytes
    
     Όπως φαίνεται απο τις κλάσεις _class L1I(L1_ICache), class L1D(L1_DCache) , class L2(L2_cache)_ του αρχείου _**devices.py**_.

  **Ερώτημα 2**
  
  Στο δεύτερο ερώτημα καλούμαστε να επαληθεύσουμε τις παραπάνω παραμέτρους με βάση τα αρχεία _**config.ini / config.json**_
  
  Πράγματι στο _config.ini_ βλέπουμε τα εξής:
  
 * Η **συχνότητα** λειτουργείας του επεξεργαστή είναι _**4Ghz**_και φαίνεται απο το _**clock=250 picoseconds**_ => (1/250)*10^12=_**4Ghz**_
  
  ```
   [system.cpu_cluster.clk_domain]
   type=SrcClockDomain
   clock=250
  ```
 * **Τύπος** επεξεργαστή _**minor**_ και **αριθμός** επεξεργαστών = **1**
  ```
  [system.cpu_cluster.cpus]
  type=MinorCPU
   ...
  numThreads=1
  ```
* Το **μέγεθος** της **μνήμης** είναι _**2Gb**_
 
 ```
 [system]
 ...
 mem_ranges=0:2147483647
 ...
 ```
 * Η **τάση** λειτουργείας του **συστήματος** και το _**clock**_, _**3.3V**_ και **_1Ghz_** αντίστοιχα καθώς και η τάση λειτουργίας του επεξεργαστή _**1.2V**_
 ```
 [system.voltage_domain]
   ...
 voltage=3.3
   ...
 ```
 ```
 [system.clk_domain]
 ...
 clock=1000
 ...
 ```
 ```
 [system.cpu_cluster.voltage_domain]
 ...
 voltage=1.2
 ...
 ```
* Η _**L1I**_ έχει _**size='48kb'**_,_**assoc=3**_,_**response latency=1ns**_
 
 ```
 [system.cpu_cluster.cpus.icache]
  ...
  assoc=3
  size=49152
  response_latency=1
  ...
 ```
* Η _**L1D**_ έχει _**size='32kb'**_,_**assoc=2**_,_**response latency=1ns**_
 
 ```
 [system.cpu_cluster.cpus.dcache]
  ...
  assoc=2
  size=32768
  response_latency=1
  ...
 ```
* Η _**L2**_ έχει _**size='1mb'**_,_**assoc=16**_,_**response latency=5ns**_
 
 ```
 [system.cpu_cluster.l2]
  ...
  assoc=16
  size=1048576
  response_latency=5
  ...
 ```
 * _**Cache Line Size**_ = 64bytes
 
 ```
 [system]
 ...
 cache_line_size=64
 ...
 ```
 
 **Ερώτημα 3**
 
 Υπάρχουν δύο _**in-order**_ είδη μοντέλων _CPU_, τα _**simplified**_ και το _**minorCPU**_.
 
* Αρχικά, τα _**simplified**_ μοντέλα χρησιμεύουν στη **γρήγορη** προσομοίωση αγνοώντας ορισμένες λεπτομέρειες και είναι κατάλληλο για απλά _test_. Η βασική κατηγορία των _simplified CPU_ μοντέλων λέγεται _**BaseSimpleCPU**_ που ορίζει βασικές συναρτήσεις για διαχείριση των _interruptions_, για _fetching_ των αιτημάτων, προετοιμασία της εκτέλεσης, διαχείρισης των ενεργειών μετά την εκτέλεση. Η βασική του λειτουργία λοιπόν ειναι να προχωράει το πρόγραμμα με **απλό** τρόπο στην επόμενη εντολή, χωρίς _pipeline_.
Για να τρέξουμε σε _BaseSimpleCPU_ μοντέλο, πρέπει να χρησιμοποιήσουμε είτε την κλάση _**AtomicSimpleCPU**_ είτε την _**TimingSimpleCPU**_ που κληρονομεί την κλάση _BaseSimpleCPU_.

   * Το μοντέλο _**AtomicSimpleCPU**_ είναι η έκδοση του _SimpleCPU_ που χρησιμοποιεί _**atomic memory accesses**_. Χρησιμοποιεί τις εκτιμήσεις χρόνου καθυστέρησης από τα _atomic accesses_ για τον υπολογισμό του συνολικού χρόνου πρόσβασης στην cache. Υλοποιεί συναρτήσεις για την ανάγνωση και εγγραφή της μνήμης, καθώς και για τον προσδιορισμό του τι γίνεται σε καθε κύκλο του επεξεργαστή. Προσδιορίζει το _port_ σύνδεσης με τη μνήμη και συνδέει τον επεξεργαστή με την _cache_.
   
   (**_Atomic access_** είναι το **γρηγορότερο** απο τα 3 είδη _accesses_, χρησιμοποιείται για γρήγορο _simulation_ και για φόρτωση στην _cache_ εν δυνάμει χρήσιμων στοιχείων, και επιστρέφουν τον ακριβή χρόνο ολοκλήρωσης του εκάστοτε αιτήματος χωρίς να περιλαμβάνουν "χρονικές απώλειες" από πόρους που είναι υπό χρήση ή από καθυστέρηση ουράς. Επιστρέφει αποτελέσματα αμέσως στο τέλος της συνάρτησης.)
   
   ![AtomicSimpleCPU](http://gem5.org/wiki/images/e/e5/AtomicSimpleCPU.jpg)
   
   
   
   * Η δεύτερη έκδοση του _SimpleCPU_ είναι το _**TimingSimpleCPU**_  που χρησιμοποιεί _**timing memory access**_. Έτσι, με αυτό το μοντέλο, ουσιαστικά περιμένουμε εως ότου ολοκληρωθεί η πρόσβαση στη μνήμη και έπειτα συνεχίζουμε παρακάτω. Όπως και στο _AtomicSimpleCPU_, υλοποιούνται οι ίδιες συναρτήσεις για ανάγνωση/εγγραφή στη μνήμη και προσδιορισμό του τι συμβαίνει στον επεξεργαστή σε κάθε κύκλο, ενω παράλληλα ορίζεται και εδώ το _port_ σύνδεσης με τη μνήμη και γίνεται σύνδεση της _cache_ με τον επεξεργαστή. Τέλος, ορίζονται οι απαραίτητες συναρτήσεις για τον χειρισμό των αποκρίσεων απο τη μνήμη.

 (To _**timing memory access**_ αποτελεί μια πιο **ρεαλιστική** πρόσβαση στη μνήμη όπου μοντελοποιείται η καθυστέρηση στην ουρά των εντολών, ενώ λαμβάνονται υπόψιν οι"χρονικές απώλειες" απο πόρους που είναι υπό χρήση, σε αντίθεση με το _atomic memory access_.)
 
 ![TimingSimpleCPU](http://gem5.org/wiki/images/f/f8/TimingSimpleCPU.jpg)
 
 
* Το _**MinorCPU**_ μοντέλο, από την άλλη πλευρά, είναι πιο λεπτομερές και ρεαλιστικό, συνεπώς είναι πιο αργό είδος. Διαθέτει 4 στάδια _**pipeline**_ και είναι αρκετά παραμετροποιήσιμο, κάτι που μας επιτρέπει να προσομοιώσουμε με μεγάλη ακρίβεια συγκεκριμένο επεξεργαστή. Τα 4 στάδια _pipeline_ που διαθέτει ονομάζονται _**Fetch1**_, _**Fetch2**_, _**Decode**_ και _**Execute**_.
  1. _Fetch1_: Εδώ λαμβάνονται οι απαραίτητες εντολές απο την _cache_ και στέλνονται για χρήση από το επόμενο στάδιο του _pipeline_
  2. _Fetch2_: Εδώ, ό,τι έρχεται από το προηγούμενο στάδιο τοποθετείται στο _**input buffer**_ και διασπάται σε ξεχωριστές εντολές. Αυτές μετά πακετάρονται σε ένα διάνυσμα εντολών και προωθούνται στο στάδιο αποκωδικοποίησης (_Decode_)
  3. _Decode_: Σε αυτό το στάδιο έχουμε αποδόμηση των εντολών απο το 2ο στάδιο σε χαμηλού επιπέδου εντολές, τοποθετούνται πάλι σε ένα διάνυσμα εντολών και προωθούνται στο επόμενο σταδιο (_Execute_)
  4. _Execute_: Στο τελευταίο επίπεδο _pipeline_ εκτελούνται οι εντολές και παρέχονται οι δυνατότητες για χρήση της μνήμης.
 
 Κατά την **εκτέλεση** του προγράμματός μας με χρήση του _**MinorCPU**_ είχαμε τα εξής αποτελέσματα (από το αρχείο _stats.txt_):
 
 ```
---------- Begin Simulation Statistics ----------
final_tick                                   43662000                       # Number of ticks from beginning of simulation (restored from checkpoints and never reset)
host_inst_rate                                  37081                       # Simulator instruction rate (inst/s)
host_mem_usage                                 711588                       # Number of bytes of host memory used
host_op_rate                                    43078                       # Simulator op (including micro ops) rate (op/s)
host_seconds                                     0.60                       # Real time elapsed on the host
host_tick_rate                               72733849                       # Simulator tick rate (ticks/s)
sim_freq                                 1000000000000                       # Frequency of simulated ticks
sim_insts                                       22253                       # Number of instructions simulated
sim_ops                                         25858                       # Number of ops (including micro ops) simulated
sim_seconds                                  0.000044                       # Number of seconds simulated
sim_ticks                                    43662000                       # Number of ticks simulated
 ```
 
 Κατά την **εκτέλεση** του προγράμματός μας με χρήση του _**TimingSimpleCPU**_ είχαμε τα εξής αποτελέσματα (από το αρχείο _stats.txt_):
 
 ```
 ---------- Begin Simulation Statistics ----------
final_tick                                   58167000                       # Number of ticks from beginning of simulation (restored from checkpoints and never reset)
host_inst_rate                                 307286                       # Simulator instruction rate (inst/s)
host_mem_usage                                 707236                       # Number of bytes of host memory used
host_op_rate                                   353500                       # Simulator op (including micro ops) rate (op/s)
host_seconds                                     0.07                       # Real time elapsed on the host
host_tick_rate                              803743457                       # Simulator tick rate (ticks/s)
sim_freq                                 1000000000000                       # Frequency of simulated ticks
sim_insts                                       22131                       # Number of instructions simulated
sim_ops                                         25570                       # Number of ops (including micro ops) simulated
sim_seconds                                  0.000058                       # Number of seconds simulated
sim_ticks                                    58167000                       # Number of ticks simulated
 ```
 Συγκρίνοντας τα αποτελέσματα από _MinorCPU_ και _TimingSimpleCPU_ διαπιστώνουμε ότι:
 
 1. Η προσομοίωση έγινε πιο γρήγορα (όσον αφορά στον χρόνο που δούλευε ο **υπολογιστής** μας) στην περίπτωση του _TimingSimpleCPU_ κάτι που δικαιολογείται από το γεγονός ότι είναι πιο απλοϊκό μοντέλο από το _MinorCPU_ (όπως αναφέραμε στην επεξήγηση των μοντέλων, αγνοεί λεπτομέρειες) 
 
 **_MinorCPU_**
 ```
 host_seconds                                      0.60
 ```
 vs
 
 
 **_TimingSimpleCPU_**
 ```
 host_seconds                                     0.07 
 ```

  2. Ο **προσομοιωτής** μας χρειάστηκε **λιγότερα** _"ticks"_ στην περίπτωση του _MinorCPU_ σε σχέση με το μοντέλο _TimingSimpleCPU_ καθώς ο _MinorCPU_ διαθέτει _pipeline_ άρα ο επεξεργαστής μπορεί να εκτελεί πολλαπλές εντολές χωρίς να περιμένει το τέλος της κάθε μίας για να προχωρήσει στην επόμενη.
  
 **_MinorCPU_**
 ```
 sim_seconds                                  0.000044                       
 sim_ticks                                    43662000
 ```
 vs
 
 
 **_TimingSimpleCPU_**
 ```
 sim_seconds                                  0.000058                       
 sim_ticks                                    58167000
 ```
 Στη συνέχεια **αλλάξαμε** κάποιες **παραμέτρους** και πήραμε τα εξής αποτελέσματα για το κάθε _**configuration**_:
 
 _**MinorCPU**_ @_**3Ghz**_ με _**DDR4_2400_16x4**_:
 ```
 ---------- Begin Simulation Statistics ----------
final_tick                                   37475487                       # Number of ticks from beginning of simulation (restored from checkpoints and never reset)
host_inst_rate                                 117718                       # Simulator instruction rate (inst/s)
host_mem_usage                                 711592                       # Number of bytes of host memory used
host_op_rate                                   136694                       # Simulator op (including micro ops) rate (op/s)
host_seconds                                     0.19                       # Real time elapsed on the host
host_tick_rate                              198076081                       # Simulator tick rate (ticks/s)
sim_freq                                 1000000000000                       # Frequency of simulated ticks
sim_insts                                       22253                       # Number of instructions simulated
sim_ops                                         25858                       # Number of ops (including micro ops) simulated
sim_seconds                                  0.000037                       # Number of seconds simulated
sim_ticks                                    37475487                       # Number of ticks simulated
 ```
_**MinorCPU**_ @_**3Ghz**_ με _**DDR3_1600_8x8**_:
```
---------- Begin Simulation Statistics ----------
final_tick                                   38216412                       # Number of ticks from beginning of simulation (restored from checkpoints and never reset)
host_inst_rate                                 111465                       # Simulator instruction rate (inst/s)
host_mem_usage                                 711588                       # Number of bytes of host memory used
host_op_rate                                   129437                       # Simulator op (including micro ops) rate (op/s)
host_seconds                                     0.20                       # Real time elapsed on the host
host_tick_rate                              191271028                       # Simulator tick rate (ticks/s)
sim_freq                                 1000000000000                       # Frequency of simulated ticks
sim_insts                                       22253                       # Number of instructions simulated
sim_ops                                         25858                       # Number of ops (including micro ops) simulated
sim_seconds                                  0.000038                       # Number of seconds simulated
sim_ticks                                    38216412                       # Number of ticks simulated
```
_**TimingSimpleCPU**_ @_**3Ghz**_ με _**DDR4_2400_16x4**_:
```
---------- Begin Simulation Statistics ----------
final_tick                                   47791494                       # Number of ticks from beginning of simulation (restored from checkpoints and never reset)
host_inst_rate                                 329496                       # Simulator instruction rate (inst/s)
host_mem_usage                                 707240                       # Number of bytes of host memory used
host_op_rate                                   379956                       # Simulator op (including micro ops) rate (op/s)
host_seconds                                     0.07                       # Real time elapsed on the host
host_tick_rate                              709848126                       # Simulator tick rate (ticks/s)
sim_freq                                 1000000000000                       # Frequency of simulated ticks
sim_insts                                       22131                       # Number of instructions simulated
sim_ops                                         25570                       # Number of ops (including micro ops) simulated
sim_seconds                                  0.000048                       # Number of seconds simulated
sim_ticks                                    47791494                       # Number of ticks simulated
```
_**TimingSimpleCPU**_ @_**3Ghz**_ με _**DDR3_1600_8x8**_:
```
---------- Begin Simulation Statistics ----------
final_tick                                   48136482                       # Number of ticks from beginning of simulation (restored from checkpoints and never reset)
host_inst_rate                                 308772                       # Simulator instruction rate (inst/s)
host_mem_usage                                 707240                       # Number of bytes of host memory used
host_op_rate                                   355821                       # Simulator op (including micro ops) rate (op/s)
host_seconds                                     0.07                       # Real time elapsed on the host
host_tick_rate                              669540765                       # Simulator tick rate (ticks/s)
sim_freq                                 1000000000000                       # Frequency of simulated ticks
sim_insts                                       22131                       # Number of instructions simulated
sim_ops                                         25570                       # Number of ops (including micro ops) simulated
sim_seconds                                  0.000048                       # Number of seconds simulated
sim_ticks                                    48136482                       # Number of ticks simulated
```
Συγκρίνοντας τα: 
1. _**MinorCPU 2Ghz + DDR3**_ (το αρχικό) με το _**MinorCPU 3Ghz + DDR3**_ και _**TimingSimpleCPU 2Ghz + DDR3**_ με το _**TimingSimpleCPU 3Ghz + DDR3**_ Παρατηρούμε ότι και στα 2 μοντέλα με αύξηση της συχνότητας ο χρόνος που χρειάστηκε ο προσομοιωτής ήταν μικρότερος (είχαμε λιγότερα _ticks_) κάτι που είναι λογικό διότι αυξάνοντας τη συχνότητα έχουμε περισσότερους κύκλους επεξεργαστή στη μονάδα του χρόνου. Αντίθετα , παρατηρούμε ότι ο αριθμός των λειτουργιών (_**sim_ops**_)που προσομοιώθηκαν παρέμεινε αμετάβλητος με την αλλαγή της συχνότητας κατι που ειναι φυσιολόγικο καθώς με την αλλαγη της συχνότητας πετύχαμε απλή επιτάχυνση του συνόλου των λειτουργιών.

_**MinorCPU 2Ghz + DDR3**_
```
 sim_seconds                                  0.000044                       
 sim_ticks                                    43662000
 ...
 sim_ops                                         25858 
 ```
 vs 
 
_**MinorCPU 3Ghz + DDR3**_
```
sim_seconds                                  0.000038                       # Number of seconds simulated
sim_ticks                                    38216412                       # Number of ticks simulated
...
sim_ops                                         25858 
```
_**TimingSimpleCPU 2Ghz + DDR3**_
```
sim_seconds                                  0.000058                       
sim_ticks                                    58167000
...
sim_ops                                         25570
```
vs

_**TimingSimpleCPU 3Ghz + DDR3**_
```
sim_seconds                                  0.000048                       # Number of seconds simulated
sim_ticks                                    48136482                       # Number of ticks simulated
...
sim_ops                                         25570
```
2. _**MinorCPU 3Ghz + DDR3**_ με το _**MinorCPU 3Ghz + DDR4**_ και _**TimingSimpleCPU 3Ghz + DDR3**_ με το _**TimingSimpleCPU 3Ghz + DDR4**_ Απο ότι βλέπουμε σε αυτή τη σύγκριση η γρηγορότερη μνήμη δεν επηρέασε πολύ τα αποτελέσματα, καθώς τα _read/write_ στη μνήμη ήταν ελάχιστα. Σε προγράμματα που κάνουν μεγαλύτερη χρήση μνήμης θα βλέπαμε πιο έντονη βελτίωση με γρηγορότερη _ram_.

_**MinorCPU 3Ghz + DDR3**_
```
sim_seconds                                  0.000038                       # Number of seconds simulated
sim_ticks                                    38216412                       # Number of ticks simulated
```
vs

_**MinorCPU 3Ghz + DDR4**_
```
sim_seconds                                  0.000037                       # Number of seconds simulated
sim_ticks                                    37475487                       # Number of ticks simulated
```

_**TimingSimpleCPU 3Ghz + DDR3**_
```
sim_seconds                                  0.000048                       # Number of seconds simulated
sim_ticks                                    48136482                       # Number of ticks simulated
```
vs

_**TimingSimpleCPU 3Ghz + DDR4**_

```
sim_seconds                                  0.000048                       # Number of seconds simulated
sim_ticks                                    47791494                       # Number of ticks simulated
```



Για τις πληροφορίες χρησιμοποιήσαμε τα στοιχεία που βρήκαμε στα εξής link:

* [link1](https://github.com/arm-university/arm-gem5-rsk/blob/master/gem5_rsk.pdf)
* [link2](http://gem5.org/SimpleCPU)
* [link3](http://gem5.org/Memory_System)


**Κριτική του πρώτου εργαστηρίου:**
 
 Το πρώτο εργαστήριο ήταν εισαγωγικό με στόχο την εξοικείωση με το εργαλείο _gem5_. Σε πρώτη φάση φαίνεται απλοϊκό, αλλά στην πράξη συναντήσαμε μικρές δυσκολίες, που δικαιολογούν την ανάγκη για αφιέρωση ενός εργαστηρίου στην εισαγωγή σε αυτό το πρόγραμμα.
 Η εκφώνηση του εργαστηρίου ήταν αρκετά περιεκτική και κατατοπιστική, χωρίς να αφήνει περιθώρια παρερμήνευσης των ζητουμένων και το βασικότερο περιείχε _link_ από πηγές για να βρούμε τις κατάλληλες πληροφορίες (όπως το _tutorial_ για το _markdown_), καθώς επίσης αναλυτικά γραμμένες εντολές για την εγκατάσταση του _gem5_ και για την εκτέλεσή του (που σε διαφορετική περίπτωση θα αφιερώναμε άσκοπα χρόνο σε _troubleshooting_, ειδικά κατά την εγκατάσταση). 
