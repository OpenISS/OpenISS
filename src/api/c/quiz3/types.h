/****
 * types.h
 *
 * Some common data types definitions
 *
 * Serguei Mokhov
 */

#ifndef _TYPES_H
#define _TYPES_H

#include <sys/types.h>
#include <sys/ipc.h>
#include <sys/sem.h>

/* Portability paranoia */
#define NULLPTR (char*) 0

/* bool data type. I like it. */
#ifndef bool
	typedef enum
	{
		false = 0,
		true  = 1
	} bool;
#endif

/* This part was borowed from some man-page on semaphores */
#if defined(__GNU_LIBRARY__) && !defined(_SEM_SEMUN_UNDEFINED)
/* union semun is defined by including <sys/sem.h> */
#else
/* according to X/OPEN we have to define it ourselves */
union semun
{
	   int                 val;   /* value for SETVAL */
	   struct semid_ds*    buf;   /* buffer for IPC_STAT, IPC_SET */
	   unsigned short int* array; /* array for GETALL, SETALL */
	   struct seminfo*     __buf; /* buffer for IPC_INFO */
};
#endif

#endif /* _TYPES_H */
