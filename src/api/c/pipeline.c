#include <stdio.h>

#include "OpenISSPipeline.h"
#include "GL2Viewer.h"

#include "vfx/Kinect1Depth.h"
#include "vfx/Kinect1RGB.h"
#include "vfx/Kinect1PCL.h"

t_iss_state g_tISSStage;

t_iss_vfx_ops* g_apvfx[VFX_END_CLOSING + 1];

int iss_init()
{
    printf("pipeline: iss_init()\n");
    
    if(vfx_kinect1_depth_module_init() != 0)
    {
        fprintf(stderr, "pipeline: vfx_kinect1_depth_module_init() failed\n");
    }

    if(vfx_kinect1_rgb_module_init() != 0)
    {
        fprintf(stderr, "pipeline: vfx_kinect1_rgb_module_init() failed\n");
    }

    if(vfx_kinect1_pcl_module_init() != 0)
    {
        fprintf(stderr, "pipeline: vfx_kinect1_pcl_module_init() failed\n");
    }

    g_tISSStage.m_eCurrentEffect = VFX_TEST_START;

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
    
    viewer_init(0, NULL);
 
    return 0;
}

int g_iFrameCutOff = 30;

void iss_draw()
{
    printf("iss_draw: Current effect: %d, time: %d\n", g_tISSStage.m_eCurrentEffect, g_tISSStage.m_iTime);
    
    if(g_apvfx[g_tISSStage.m_eCurrentEffect] != NULL)
    {
        g_apvfx[g_tISSStage.m_eCurrentEffect]->vfx_draw();
    }
    else
    {
        printf("pipeline: skipping\n");
        g_tISSStage.m_eCurrentEffect = (g_tISSStage.m_eCurrentEffect + 1) % VFX_END_CLOSING;
        printf("iss_draw: new effect: %d, time: %d\n", g_tISSStage.m_eCurrentEffect, g_tISSStage.m_iTime);
        return;
    }
    
    g_tISSStage.m_iTime++;
    
    if(g_tISSStage.m_iTime % g_iFrameCutOff == 0)
    {
        printf("pipeline: effect changeover due to timer\n");
        g_tISSStage.m_eCurrentEffect = (g_tISSStage.m_eCurrentEffect + 1) % VFX_END_CLOSING;
        printf("iss_draw: new effect: %d, time: %d\n", g_tISSStage.m_eCurrentEffect, g_tISSStage.m_iTime);
    }
    
    if(g_tISSStage.m_iTime >= (VFX_END_CLOSING + 1) * g_iFrameCutOff)
    {
        printf("pipeline: timer reset to 0\n");
        g_tISSStage.m_iTime = 0;
        printf("iss_draw: new effect: %d, time: %d\n", g_tISSStage.m_eCurrentEffect, g_tISSStage.m_iTime);
    }
}

void iss_free()
{
    printf("pipeline:iss_free()\n");
    
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
