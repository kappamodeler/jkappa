Example files:

    Example.ka                         from KappaFactory, Model: "ras.cascade.1"

    brightberl.ka                      copied from kappa_models/models/brightberl/brightberl.ka    
                                       everything disappears with --time 1000
    
    large_systems-sysepi.ka            copied from kappa_models/models/sysepi/sysepi.ka     
                                       run it at least with --time 1000
    
    degradation-deg-all.ka             copied from kappa_models/models/degradation/deg-all.ka      
                                       steady state at --time 10
    
    degradation-deg-free.ka            copied from kappa_models/models/degradation/deg-free.ka      
                                       steady state at --time 40
    
    degradation-deg-bnd.ka             copied from kappa_models/models/degradation/deg-bnd.ka      
                                       steady state at --time 10
   
    contextual_rules-add_rem.ka        copied from kappa_models/models/debugging/add_rem.ka    
                                       steady state at  --time 10
    
    SIA_2007_03_23-egfr.ka             copied from demos/SIA_2007_03_23/egfr.ka   
                                       steady state oscillations --time 15
    
    fgf2-fgf.ka                        copied from kappa_models/models/fgf2/fgf.ka   
                                       steady state with --time 15
    
    KPT_study.ka                       copied from kappa_factory/trunk/samples/kappa/KPT_study.ka      
                                       steady state at  --time 10
    
    large_systems-sorger.ka            copied from kappa_models/models/sorger/sorger.ka     
                                       big system, long simulation so had to give a very large time: --time 150

    egfr.ka                            copied from kappa_factory/trunk/samples/kappa/egfr.ka 
                                       the same ruleset as SIA_2007_03_23-egfr.ka, just different initial conditions and observables
                                       steady state at  --time 10

    osc.ka                             copied from kappa_models/models/oscillateurs/osc.ka
                                       nice oscillations. use --time 250

    exponentielle.ka                   copied from kappa_models/models/exponentielle/exponentielle.ka
                                       nice exponential curve. use --time 10

    invexp.ka                          copied from kappa_models/models/invexp/invexp.ka
                                       nice inverse curve. use --time 5

    SIA_2007_03_23-global.ka           copied from demos/SIA_2007_03_23/global.ka
                                       this is also the same file as kappa_models/models/unbounded_entropy/unbounded_entropy.ka
                                       nice curve --time 20

    TyThomson-ReceptorAndGProtein.ka   part of Ty Thomson's yeast model. Manually edited
                                       run with --time 200
    
    calcium2.ka                        copied from kappa_models/models/calcium2/calcium2.ka
                                       steady state at --time 0.5

    debugging-bug_update.ka            copied from kappa_models/models/debugging/bug_update.ka
                                       run with --time 10

    debugging-compression.ka           copied from kappa_models/models/debugging/compression.ka
                                       long simulation... run with AT LEAST --time 100

    debugging-inf.ka                   copied from kappa_models/models/debugging/inf.ka
                                       run with --time 100
       
    debugging-justin-9-07-2008.ka      copied from kappa_models/models/debugging/justin-9-07-2008.ka
                                       long simulation... run with --time 500

    debugging-link.ka                  copied from kappa_models/models/debugging/link.ka
                                       2 observables are always equal... run with --time 50

    debugging-polymere.ka              copied from kappa_models/models/debugging/polymere.ka
                                       run with --time 50

    debugging-semi-link-bug.ka         copied from kappa_models/models/debugging/semi-link-bug.ka
                                       run with --time 25

    debugging-walter-fev08.ka          copied from kappa_models/models/debugging/walter-fev08.ka
                                       run with --time 25
    
    debugging-weird.ka                 copied from kappa_models/models/debugging/weird.ka
                                       run with --time 10

    sfb.ka                             copied from kappa_factory/trunk/test_models/sfb.ka
                                       run with --time 100
 
    will.ka                            copied from kappa_factory/trunk/test_models/will.ka
                                       long simulation. very nice oscillations with some transients. run with --time 1000

    side_effect.ka                     copied from kappa_models/models/side_effect/side_effect.ka
                                       nice decays. run with --time 10

    scalability-bench-mapk1.ka         copied from research/papers/scalability/bench/mapk1.ka
                                       run with --time 0.5

    repressilator.ka                   copied from kappa_models/models/repressilator/repressilator.ka 
                                       interesting curves! run with --time 10000

    non_atomic.ka                      copied from kappa_models/models/QA/non_atomic/na.ka
                                       run with --time 20

    add_linked_species.ka              copied from kappa_models/models/add_linked_species/add_linked_species.ka 
                                       run with --time 0.01

    abc2.ka                            copied from kappa_models/models/abc2/abc2.ka
                                       simulation with perturbation
                                       run with --time 25 for observing perturbation effects
                                       run with --time 0.05 for observing initial transients

    MekScaf.ka                         copied from kappa_factory/trunk/samples/kappa/MekScaf.ka
                                       run with --time 0.02

    eric.ka                            copied from kappa_models/models/eric/eric.ka 
                                       run with --time 0.002

    half_binding.ka                    copied from kappa_models/models/half_binding/half_binding.ka 
                                       run with --time 0.05 for initial changes
                                       run with --time 10 for long term steady state

    easy-egfr.ka                       copied from kappa_models/models/easy/egfr.ka
                                       run with --time 0.02

    erasure.ka                         copied from kappa_models/models/erasure/erasure.ka
                                       run with --time 10

    Seda_111008_Insulin_Present.ka	   received it from Isha in Seda_111008.xml KF file
                                       run with --time 15

    ABC_ring.ka						   received it from Ty via https://jira.plectix.com/browse/ENG-275
									   run with --event 1000
									   the final state doesn't have any observable "ABC ring"
									   therefore the count for observable "ABC ring" should be zero. All non-zero numbers are the result of a bug 

    Initialization-bug.ka			   received from Ty via https://jira.plectix.com/browse/ENG-301
									   According to this bug, the initialization step for this model is really really slow in JSIM (~150s) compared to simplx (~1s)

    Symmetry.ka						   received from Ty via https://jira.plectix.com/browse/ENG-305
									   rule activity must be divided by the symmetry of the rule. this file includes a few rules with different symmetries.
									   we should make sure that the rule activities are computed correctly and match simplx

    eugen-ENG-312.ka				   received from Ty via https://jira.plectix.com/browse/ENG-312
									   Note that the rule and the agent A are just there so something happens. 
									   The real interesting stuff is with the complexes involving ULF, which don't change, so the observables are essentially measuring the initial conditions. 
									   The results reported by jsim for the observable 'size 3' are wrong.

    tami-ENG-313.ka					   received from Ty via https://jira.plectix.com/browse/ENG-313
									   if you run it for 100 events it runs without an error, so the bug is dependent on the simulation time. try:
									   --sim tami-ENG-313.ka --no-maps --event 10000 --points 1000 --output-final-state --rescale 1.0 --xml-session-name tmp.xml 
									
    ENG-326.ka 					       received from Ty via https://jira.plectix.com/browse/ENG-326
									   The results of JSIM (Plectix-Simulator-20091102) and simplx (SIMulator by PLectiX: 4.2..10037_32) disagree!
									   Command line parameters are: --no-maps --time 100 


Options for simulator:
	--compile name of the kappa file to compile
	--sim name of the kappa file to simulate
	--time (infinite): time units of computation
	--seed Seed the random generator using given integer (same integer will generate the same random number sequence)
	--xml_session_name Name of the xml file containing results of the current session (default simplx.xml)

