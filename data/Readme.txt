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
	                                   steady state at  --time 10

	osc.ka                             copied from kappa_models/models/oscillateurs/osc.ka
	                                   nice oscillations. use --time 250

	exponentielle.ka                   copied from kappa_models/models/exponentielle/exponentielle.ka
	                                   nice exponential curve. use --time 10

	invexp.ka                          copied from kappa_models/models/invexp/invexp.ka
	                                   nice inverse curve. use --time 5

	SIA_2007_03_23-global.ka           copied from demos/SIA_2007_03_23/global.ka
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
									


Options for simulator:
	--compile name of the kappa file to compile
	--sim name of the kappa file to simulate
	--time (infinite): time units of computation
	--seed Seed the random generator using given integer (same integer will generate the same random number sequence)
	--xml_session_name Name of the xml file containing results of the current session (default simplx.xml)
