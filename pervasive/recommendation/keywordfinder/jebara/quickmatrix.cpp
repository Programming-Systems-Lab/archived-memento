

/* Simple Vector data type. */
typedef struct VectorRec {
  double *d;
  int len;
} *Vector;


typedef struct MatrixRec {
  double *d;
  int len;
  int cols;
  double **d2;
  int rows;
} *Matrix;


#define Allocate(n,t) (t*)malloc((n)*sizeof(t))
#define AllocateC(n,t) (t*)calloc(n, sizeof(t))
#define Reallocate(p, n, t) ((p)=(t*)realloc((p),(n)*sizeof(t)))
#define AllocCopy(n,t,v) (t*)memcpy(Allocate(n,t),v,(n)*sizeof(t))

#ifndef max
#define max(a,b) ((a)>(b)?(a):(b))
#endif
#ifndef min
#define min(a,b) ((a)<(b)?(a):(b))
#endif


Vector VectorFromData(int length, double *data)
{
  Vector v;

  v = Allocate(1, struct VectorRec);
  v->len = length;
  v->d = data;
  return v;
}

/* Multiply all elements of v by x. */
void VectorScale(Vector v, double x)
{
  int i;
  for(i=0;i<v->len;i++) v->d[i] *= x;
}


/* Returns the sum of all elements of v. */
double VectorSum(Vector v)
{
  int i;
  double sum = 0.0;
  for(i=0;i<v->len;i++) sum += v->d[i];
  return sum;
}


#define VectorMove(v1, v2) memcpy((v1)->d, (v2)->d, (v2)->len*sizeof(double))


/* Returns a new, uninitialized Vector whose length is given. */
Vector VectorCreate(int length)
{
  double *data = Allocate(length, double);
  if(!data) {
    fprintf(stderr, "Cannot allocate %d element vector\n", length);
    return NULL;
  }
  return VectorFromData(length, data);
}


/* Compute vector dot product = VectorSum(VectorMultiply(v1,v2))
 * of v1 and v2. 
 */
double VectorDot(Vector v1, Vector v2)
{
  int i;
  double sum = 0.0;
  for(i=0;i<v1->len;i++) sum += v1->d[i] * v2->d[i];
  return sum;
}

/* Set all elements of v to x. */
void VectorSet(Vector v, double x)
{
  int i;
  for(i=0;i<v->len;i++) v->d[i] = x;
}

/* Frees all storage associated with v. */
void VectorFree(Vector v)
{
  free(v->d);
  free(v);
}

/* Returns a Matrix whose data is given. The array becomes property 
 * of the Matrix and will be freed when the Matrix is. The data must be
 * rows*cols elements long.
 */
Matrix MatrixFromData(int rows, int cols, double *data)
{
  Matrix m;
  int i;

  m = Allocate(1, struct MatrixRec);
  m->rows = rows;
  m->cols = cols;
  m->len = rows*cols;
  m->d = data;
  m->d2 = Allocate(rows, double*);
  for(i=0;i<rows;i++) m->d2[i] = data + i*cols;
  return m;
}

/* Returns a new, uninitialized Matrix whose dimensions are given. */
Matrix MatrixCreate(int rows, int cols)
{
  double *data = Allocate(rows*cols, double);
#if 0
  if(!data) {
    fprintf(stderr, "Cannot allocate %d by %d matrix\n", rows, cols);
    return NULL;
  }
#endif
  return MatrixFromData(rows, cols, data);
}

/* Frees all storage associated with m. */
void MatrixFree(Matrix m)
{
  free(m->d2);
  free(m->d);
  free(m);
}

void MatrixVectorMultiply(Vector dest, Matrix a, Vector v)
{
  Vector t = dest;
  int i,j;
  assert(dest->len >= a->rows);
  assert(a->cols == v->len);
  if(dest == v) {
    t = VectorCreate(dest->len);
  }
  for(i=0;i<a->rows;i++) {
    double sum = 0;
    for(j=0;j<v->len;j++) {
      sum += a->d2[i][j] * v->d[j];
    }
    t->d[i] = sum;
  }
  if(t != dest) {
    VectorMove(dest, t);
    VectorFree(t);
  }
}




