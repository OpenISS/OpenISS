#include <stdio.h>

#include "OpenISSPipeline.h"

#include "vfx/Kinect1Depth.h"

t_iss_state g_tISSStage;

t_iss_vfx_ops* g_apvfx[VFX_END_CLOSING + 1];

int iss_init()
{
    if(vfx_kinect1_depth_module_init() != 0)
    {
         fprintf(stderr, "pipeline: vfx_kinect1_depth_module_init() failed\n");
    }
  
    g_tISSStage.m_eCurrentEffect = VFX_TEST_START;
    //g_apvfx[g_tISSStage.m_eCurrentEffect]->vfx_init();

    int i, l_iFirstInitialized;
    e_vfx_enum l_eFirstInitialized;
    
    l_iFirstInitialized = -1;
    
    for(i = 0; i < VFX_END_CLOSING + 1; i++)
    {
        if(g_apvfx[i] != NULL)
        {
             if(l_iFirstInitialized < 0)
             {
                 l_iFirstInitialized = i;
                 l_eFirstInitialized = i;
             }

             printf("Initializing effect: %d\n", i);
             g_apvfx[i]->vfx_init();
        }
    }
 
    g_tISSStage.m_eCurrentEffect = l_eFirstInitialized;
 
    return 0;
}

void iss_draw()
{
    printf("Current effect: %d\n", g_tISSStage.m_eCurrentEffect);
    g_apvfx[g_tISSStage.m_eCurrentEffect]->vfx_draw();
}

void iss_free()
{
    printf("pipeline:iss_free()");
    int i;
    
    for(i = 0; i < VFX_END_CLOSING + 1; i++)
    {
        if(g_apvfx[i] != NULL)
        {
             printf("Freeing effect: %d\n", i);
             g_apvfx[i]->vfx_free();
        }
    }
}

/* EOF */
