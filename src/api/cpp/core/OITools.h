#ifndef OPENISS_OITOOLS_H
#define OPENISS_OITOOLS_H

#include <iostream>
#include <OpenNI.h>

namespace openiss {

// this template is borrow from openni2
template<class T>
class Array {
public:
    Array() : m_data(NULL), m_count(0), m_owner(false) {}
    Array(const T *data, int count) : m_owner(false) { _setData(data, count); }
    ~Array() { clear(); }

    int getSize() const { return m_count; }
    const T &operator[](int index) const { return m_data[index]; }
    void _setData(const T *data, int count, bool isOwner = false) {
        clear();
        m_count = count;
        m_owner = isOwner;
        if (!isOwner) {
            m_data = data;
        }
        else {
            m_data = new T[count];
            memcpy((void *) m_data, data, count * sizeof(T));
        }
    }

private:
    Array(const Array<T> &);
    Array<T> &operator=(const Array<T> &);

    void clear() {
        if (m_owner && m_data != NULL) {
            delete[] m_data;
        }
        m_owner = false;
        m_data = NULL;
        m_count = 0;
    }

    const T *m_data;
    int m_count;
    bool m_owner;
};

class OIUtilities {
public:
    static void checkStatus(openni::Status status, const char *msg) {
        if (status != openni::STATUS_OK) {
            std::cout << msg << std::endl;
            std::cout << openni::OpenNI::getExtendedError() << std::endl;
            exit(1);
        }
    };

    static void checkStatus(nite::Status status, const char *msg) {
        if (status != nite::STATUS_OK) {
            std::cout << msg << std::endl;
            exit(1);
        }
    };

};
}
#endif //OPENISS_OITOOLS_H

